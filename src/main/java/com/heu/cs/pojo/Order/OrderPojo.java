package com.heu.cs.pojo.Order;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by memgq on 2017/5/17.
 */
public class OrderPojo {
private String orderId="";
    private LocationPojo endLocation;
    private LocationPojo startLocation;
    private String sender;
    private String receiver;
    private String senderTel;
    private String receiverTel;
    private String goodsName;
    private String orderOwnerId;
    private String orderReceiverId;
    private String remark;
    private String sendTime;//期望配送员取货时间
    private String receiveTime;//期望物品送达时间
    private String putOrderTime;//下单时间
    private String receiveOrderTime;//接单时间
    private String deliveryTime;//真正送达时间
    private String imagePath;
    private String orderStatus;
    private String orderPrice;
    private String orderReplyCode;
    private String emergency;
    private String commit="-1";

    public String getCommit() {
        return commit;
    }

    public void setCommit(String commit) {
        this.commit = commit;
    }

    public String getEmergency() {
        return emergency;
    }

    public void setEmergency(String emergency) {
        this.emergency = emergency;
    }

    public String getOrderReplyCode() {
        return orderReplyCode;
    }

    public void setOrderReplyCode(String orderReplyCode) {
        this.orderReplyCode = orderReplyCode;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getOrderReceiverId() {
        return orderReceiverId;
    }

    public void setOrderReceiverId(String orderReceiverId) {
        this.orderReceiverId = orderReceiverId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public LocationPojo getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LocationPojo endLocation) {
        this.endLocation = endLocation;
    }

    public LocationPojo getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LocationPojo startLocation) {
        this.startLocation = startLocation;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSenderTel() {
        return senderTel;
    }

    public void setSenderTel(String senderTel) {
        this.senderTel = senderTel;
    }

    public String getReceiverTel() {
        return receiverTel;
    }

    public void setReceiverTel(String receiverTel) {
        this.receiverTel = receiverTel;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getOrderOwnerId() {
        return orderOwnerId;
    }

    public void setOrderOwnerId(String orderOwnerId) {
        this.orderOwnerId = orderOwnerId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getPutOrderTime() {
        return putOrderTime;
    }

    public void setPutOrderTime(String putOrderTime) {
        this.putOrderTime = putOrderTime;
    }

    public String getReceiveOrderTime() {
        return receiveOrderTime;
    }

    public void setReceiveOrderTime(String receiveOrderTime) {
        this.receiveOrderTime = receiveOrderTime;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<String> getAttributes(){
        List<String> attrs=new ArrayList<String>();
        Field[] field=this.getClass().getDeclaredFields();
        for(int j=0 ; j<field.length ; j++) {     //遍历所有属性
            String name = field[j].getName();
            attrs.add(name);
        }
        return attrs;
    }
}
