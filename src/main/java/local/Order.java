package local;

import javax.persistence.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import local.config.kafka.KafkaProcessor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.MimeTypeUtils;

import java.util.List;

@Entity
@Table(name="Order_table")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long orderId;
    private String product;
    private Integer qty;
    private Integer price;
    private Integer type;

    @PostPersist
    @PostUpdate
    @PrePersist
    public void eventPublish() throws JsonProcessingException {

        if (type.equals(0)) {
            OrderSelected orderSelected = new OrderSelected();
            orderSelected.setEventType("OrderSelected");
            BeanUtils.copyProperties(this, orderSelected);

            orderSelected.setType(this.getType());
            orderSelected.setOrderId(this.getOrderId());
            orderSelected.setQty(this.getQty());
            orderSelected.setProduct(this.getProduct());

            ObjectMapper objectMapper = new ObjectMapper();
            String json = null;

            try {
                json = objectMapper.writeValueAsString(orderSelected);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON format exception", e);
            }

            KafkaProcessor processor = CustomerApplication.applicationContext.getBean(KafkaProcessor.class);
            MessageChannel outputChannel = processor.outboundTopic();

            /*
            outputChannel.send(MessageBuilder
                    .withPayload(json)
                    .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                    .build()); */




        PaymentCompleted paymentCompleted = new PaymentCompleted();

        // Payment 완료되었다고 가정

        // BeanUtils.copyProperties(this, paymentCompleted);
        paymentCompleted.setType(this.getType());
        paymentCompleted.setOrderId(this.getOrderId());
        paymentCompleted.setQty(this.getQty());
        paymentCompleted.setProduct(this.getProduct());

        paymentCompleted.publishAfterCommit();
    }


        else

    {
        /*
        OrderCanceled orderCanceled = new OrderCanceled();
        BeanUtils.copyProperties(this, orderCanceled);
        orderCanceled.publishAfterCommit();
        */


        OrderCancelRequested orderCancelRequested = new OrderCancelRequested();
        BeanUtils.copyProperties(this, orderCancelRequested);
        orderCancelRequested.publishAfterCommit();

        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;

        try {
            json = objectMapper.writeValueAsString(orderCancelRequested);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON format exception", e);
        }

        KafkaProcessor processor = CustomerApplication.applicationContext.getBean(KafkaProcessor.class);
        MessageChannel outputChannel = processor.outboundTopic();

        outputChannel.send(MessageBuilder
                .withPayload(json)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build());


    }

}

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }
    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

}