package tech.cassandre.trading.bot.ta4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import org.ta4j.core.Bar;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

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
        aggregator.update(getTime("2021-01-01 10:00:00"), 10,  100);
        aggregator.update(getTime("2021-01-01 10:01:00"), 3,  300);
        aggregator.update(getTime("2021-01-01 10:02:00"), 15,  3000);
        aggregator.update(getTime("2021-01-01 10:05:00"), 20,  3300);


        assertTrue(testSubscriber.subscribed);
        testSubscriber.request(1);

        assertEquals(1, testSubscriber.bars.size());
        assertEquals(15d, testSubscriber.bars.get(0).getHighPrice().doubleValue());
        assertEquals(3d, testSubscriber.bars.get(0).getLowPrice().doubleValue());
        assertEquals(15d, testSubscriber.bars.get(0).getClosePrice().doubleValue());
        assertEquals(10, testSubscriber.bars.get(0).getOpenPrice().doubleValue());
        assertEquals(2900d, testSubscriber.bars.get(0).getVolume().doubleValue());
    }

    @DisplayName("Check that aggregation does not happen, when time between bars is equal to last timestamp + distance")
    @Test
    public void shouldNotAggregateBars() {
        aggregator.update(getTime("2021-01-01 00:00:00"), 10,100);
        aggregator.update(getTime("2021-01-01 00:05:00"), 3,300);
        aggregator.update(getTime("2021-01-01 00:10:00"), 15,1000);
        aggregator.update(getTime("2021-01-01 00:15:00"), 20,300);


        assertTrue(testSubscriber.subscribed);
        testSubscriber.request(3);

        assertEquals(3, testSubscriber.bars.size());

        Bar firstBar = testSubscriber.bars.get(0);
        Bar secondBar = testSubscriber.bars.get(1);
        Bar thirdBar = testSubscriber.bars.get(2);
        
        assertEquals(10d, firstBar.getHighPrice().doubleValue());
        assertEquals(10d, firstBar.getLowPrice().doubleValue());
        assertEquals(10d, firstBar.getClosePrice().doubleValue());
        assertEquals(10d, firstBar.getOpenPrice().doubleValue());
        assertEquals(100d, firstBar.getVolume().doubleValue());

        assertEquals(10d, secondBar.getHighPrice().doubleValue());
        assertEquals(3d, secondBar.getLowPrice().doubleValue());
        assertEquals(3d, secondBar.getClosePrice().doubleValue());
        assertEquals(10d, secondBar.getOpenPrice().doubleValue());
        assertEquals(300d, secondBar.getVolume().doubleValue());

        assertEquals(15d, thirdBar.getHighPrice().doubleValue());
        assertEquals(3d, thirdBar.getLowPrice().doubleValue());
        assertEquals(15d, thirdBar.getClosePrice().doubleValue());
        assertEquals(3d, thirdBar.getOpenPrice().doubleValue());
        assertEquals(1000d, thirdBar.getVolume().doubleValue());
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
