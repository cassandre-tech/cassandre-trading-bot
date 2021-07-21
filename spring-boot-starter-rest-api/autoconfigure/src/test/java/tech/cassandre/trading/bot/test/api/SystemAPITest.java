package tech.cassandre.trading.bot.test.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import tech.cassandre.trading.bot.test.util.APITest;
import tech.cassandre.trading.bot.test.util.BaseMock;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("API - System API test")
@Import(BaseMock.class)
public class SystemAPITest extends APITest {

    @Test
    @DisplayName("Check ping response")
    public final void checkPingResponse() throws Exception {
        mockMvc.perform(get("/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("pong")));
    }

    @Test
    @DisplayName("Check version response")
    public final void checkVersionResponse() throws Exception {
        mockMvc.perform(get("/version"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("1.0.0")));
    }

}
