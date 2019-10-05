package su.mbv.cbrf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:/application.properties")
public class AppConfig {
        @Value("${cache.livetime}")
        public static int liveTimeSec;
}
