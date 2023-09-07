package com.metoo.nspm.entity.nspm;

public class KafkaOrder {

    private Long orderId;
    private int count;

    public KafkaOrder(){};

    public Long getOrderId(){
        return this.orderId;
    };

    public int getCount(){
        return this.count;
    }

    public void setOrderId(Long orderId){
        this.orderId = orderId;
    }

    public void setCount(int count){
        this.count = count;
    }


}

