package local;

import local.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired
    OrderRepository orderRepository;
    
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderReceived_CannotOrderCencel(@Payload OrderReceived orderReceived){

        if(orderReceived.isMe()){
            System.out.println("##### listener CannotOrderCencel : " + orderReceived.toJson());
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderRejected_CannotOrderCencel(@Payload OrderRejected orderRejected){

        if(orderRejected.isMe()){
            System.out.println("##### listener CannotOrderCencel : " + orderRejected.toJson());
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderRejected_PayBack(@Payload OrderRejected orderRejected){

        if(orderRejected.isMe()){
            System.out.println("##### listener PayBack : " + orderRejected.toJson());
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderRejected_OrderCancelFromDelivery(@Payload OrderRejected orderRejected){

        if(orderRejected.isMe()){
            System.out.println("##### listener OrderCancelFromDelivery : " + orderRejected.toJson());
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCancelConfirmed_ReceiveCancelConfirmed(@Payload OrderCancelConfirmed orderCancelConfirmed){

        if(orderCancelConfirmed.isMe()){
            System.out.println("#### 오더가 취소 되었습니다. : " + orderCancelConfirmed.getOrderId().toString());
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderRejected_RejectedFromDelivery(@Payload OrderRejected orderRejected){

        if(orderRejected.isMe()){
            System.out.println("#### 오더가 거절 되었습니다. : " + orderRejected.getOrderId().toString());
            orderRepository.findById(orderRejected.getOrderId()).ifPresent(
                    order -> {
                        order.setType(2);
                        orderRepository.save(order);
                    }
            );
            System.out.println("#### 환불 되었습니다.");
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderReceived_CannotOrderCancel(@Payload OrderReceived orderReceived){

        if(orderReceived.isMe()){
            System.out.println("#### 오더가 수락 되었습니다. : " + orderReceived.getOrderId().toString());
            orderRepository.findById(orderReceived.getOrderId()).ifPresent(
                    order -> {
                        order.setType(3);
                        orderRepository.save(order);
                    }
            );
            System.out.println("#### 환불 되었습니다.");
        }
    }
}
