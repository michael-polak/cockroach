package com.cisco.td.general.cocroach;

import com.cognitivesecurity.commons.time.TimeInterval;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static com.cognitivesecurity.commons.util.Literals.list;
import static com.cognitivesecurity.commons.util.Literals.map;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class SalesReportPreparationTest {

    private SalesReportPreparation salesReportPreparation;

    @BeforeEach
    void setUp() {
        salesReportPreparation = new SalesReportPreparation();
    }

    @Test
    void oldPurchasesStillTakenAccountFor100KLimit() {
        SalesReport salesReport = salesReportPreparation.generateSalesReport(
                list(
                        new SaleRecord(
                                DateTime.parse("2021-06-30"),
                                "ESPP",
                                20,
                                50.0,
                                30.0,
                                40.0,
                                DateTime.parse("2020-06-30")
                        ),
                        new SaleRecord(
                                DateTime.parse("2021-06-30"),
                                "ESPP",
                                400,
                                40.0,
                                10.0,
                                30.0,
                                DateTime.parse("2017-06-30")
                        )
                ),
                new TimeInterval(Instant.parse("2021-01-01T00:00:00.00Z").toEpochMilli(), Instant.parse("2022-01-01T00:00:00.00Z").toEpochMilli()),
                new YearConstantExchangeRateProvider(map(2021,10.0))
        );

        assertThat(
                salesReport.getSellCroneValue(),
                is(170000.0000)
        );

        assertThat(
                salesReport.getProfitForTax(),
                is("2000.0000")
        );

    }

    @Test
    void lossInLast3YearsIsDistractedFromProfit() {
        SalesReport salesReport = salesReportPreparation.generateSalesReport(
                list(
                        new SaleRecord(
                                DateTime.parse("2021-06-30"),
                                "ESPP",
                                20,
                                50.0,
                                30.0,
                                40.0,
                                DateTime.parse("2020-06-30")
                        ),
                        new SaleRecord(
                                DateTime.parse("2021-06-15"),
                                "ESPP",
                                40,
                                40.0,
                                10.0,
                                41.0,
                                DateTime.parse("2021-05-30")
                        )
                ),
                new TimeInterval(Instant.parse("2021-01-01T00:00:00.00Z").toEpochMilli(), Instant.parse("2022-01-01T00:00:00.00Z").toEpochMilli()),
                new YearConstantExchangeRateProvider(map(2021,10.0))
        );

        assertThat(
                salesReport.getSellCroneValue(),
                is(26000.0000)
        );

        assertThat(
                salesReport.getRecentProfitCroneValue(),
                is(1600.0000)
        );

        assertThat(
                salesReport.getProfitForTax(),
                is("")
        );
    }
}