package tech.cassandre.trading.bot.test.ta4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import org.ta4j.core.Bar;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import tech.cassandre.trading.bot.util.ta4j.DurationBarAggregator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DurationBarAggregatorTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    DurationBarAggregator aggregator;
    TestSubscriber testSubscriber;

    @BeforeEach
    public void setup(){
        aggregator = new DurationBarAggregator(Duration.ofMinutes(5));
        final Flux<Bar> barFlux = aggregator.getBarFlux();
        testSubscriber = new TestSubscriber();
        barFlux.subscribe(testSubscriber);
    }

    @DisplayName("Check intra bar aggregation")
    @Test
    public void shouldAggregateBars() {
        aggregator.update(getTime("2021-01-01 10:00:00"), 10, 15, 9, 100);
        aggregator.update(getTime("2021-01-01 10:01:00"), 3, 10, 2, 300);
        aggregator.update(getTime("2021-01-01 10:02:00"), 15, 16, 10, 3000);
        aggregator.update(getTime("2021-01-01 10:05:00"), 20, 30, 16, 300);


        assertTrue(testSubscriber.subscribed);
        testSubscriber.request(1);

        assertEquals(1, testSubscriber.bars.size());
        assertEquals(16d, testSubscriber.bars.get(0).getHighPrice().doubleValue());
        assertEquals(2d, testSubscriber.bars.get(0).getLowPrice().doubleValue());
        assertEquals(15d, testSubscriber.bars.get(0).getClosePrice().doubleValue());
        assertEquals(0, testSubscriber.bars.get(0).getOpenPrice().doubleValue());
        assertEquals(3400d, testSubscriber.bars.get(0).getVolume().doubleValue());
    }

    @DisplayName("Check that aggregation does not happen, when time between bars is equal to last timestamp + distance")
    @Test
    public void shouldNotAggregateBars() {
        aggregator.update(getTime("2021-01-01 00:00:00"), 10, 15, 9, 100);
        aggregator.update(getTime("2021-01-01 00:05:00"), 3, 10, 2, 300);
        aggregator.update(getTime("2021-01-01 00:10:00"), 15, 16, 10, 3000);
        aggregator.update(getTime("2021-01-01 00:15:00"), 20, 30, 16, 300);


        assertTrue(testSubscriber.subscribed);
        testSubscriber.request(3);

        assertEquals(3, testSubscriber.bars.size());

        assertEquals(15d, testSubscriber.bars.get(0).getHighPrice().doubleValue());
        assertEquals(9d, testSubscriber.bars.get(0).getLowPrice().doubleValue());
        assertEquals(10d, testSubscriber.bars.get(0).getClosePrice().doubleValue());
        assertEquals(0, testSubscriber.bars.get(0).getOpenPrice().doubleValue());
        assertEquals(100d, testSubscriber.bars.get(0).getVolume().doubleValue());
    }

    ZonedDateTime getTime(String value){
        return LocalDateTime.parse(value, dateTimeFormatter).atZone(ZoneId.systemDefault());
    }


    private class TestSubscriber extends BaseSubscriber<Bar> {
        boolean subscribed;
        List<Bar> bars = new ArrayList<>();


        @Override
        protected void hookOnNext(Bar value) {
            super.hookOnNext(value);
            bars.add(value);
        }

        @Override
        protected void hookOnSubscribe(Subscription subscription) {
            super.hookOnSubscribe(subscription);
            subscribed = true;
        }
    }

}
