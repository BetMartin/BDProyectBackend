package org.example.config;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.MercadoPagoClient;
import com.mercadopago.client.payment.PaymentClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoConfiguration {
    @Bean
    public PaymentClient paymentClient() {
        MercadoPagoConfig.setAccessToken("APP_USR-5935710845811407-051518-d0bfb248902cd4e3c8d0738f587e902f-2435790313");
        return new PaymentClient();
    }
}
