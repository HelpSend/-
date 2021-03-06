package com.heu.cs.dao.orderdao;

import com.google.gson.Gson;
import com.heu.cs.conndb.ConnMongoDB;
import com.heu.cs.utils.GenericMethod;
import com.heu.cs.utils.GenericMethodImpl;
import com.heu.cs.pojo.Order.MyReceiveOrderPojo;
import com.heu.cs.pojo.Order.OrderPojo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by memgq on 2017/6/5.
 */
public class QueryMyReceiveOrderDao {
    public String queryMyReceiveOrder(String orderReceiverId,String orderStatus){
        String returnRes="";
        ArrayList<MyReceiveOrderPojo> myReceiveOrderPojoArrayList=new ArrayList<MyReceiveOrderPojo>();
        Gson gson=new Gson();
        ConnMongoDB connMongoDB=new ConnMongoDB();
        MongoCollection collection=connMongoDB.getCollection("bbddb","normalorder");
        Document filter=new Document();
        if(orderStatus.equals("all")){
            filter.append("orderReceiverId", orderReceiverId);
        }else if(orderStatus.equals("0")){
            List<Document> list = new ArrayList<Document>();
            list.add(new Document("orderStatus","0"));
            list.add(new Document("orderStatus","1"));
            filter.append("orderReceiverId",orderReceiverId).append("$or",list);
        }else {
            filter.append("orderReceiverId", orderReceiverId).append("orderStatus", orderStatus);
        }
        Document sortDocument=new Document();
        sortDocument.append("receiveOrderTime",-1);
        MongoCursor<Document> mongoCursor= collection.find(filter).sort(sortDocument).limit(20).iterator();
        while (mongoCursor.hasNext()){
            Document d=mongoCursor.next();
            GenericMethod genericMethod =new GenericMethodImpl();
            genericMethod.updateOrderId(d,collection);
            OrderPojo orderPojo=gson.fromJson(d.toJson(),OrderPojo.class);
            MyReceiveOrderPojo myReceiveOrderPojo=new MyReceiveOrderPojo();
            myReceiveOrderPojo.setGoodsName(orderPojo.getGoodsName());
            myReceiveOrderPojo.setOrderId(d.get("_id").toString());
            myReceiveOrderPojo.setOrderStatus(orderPojo.getOrderStatus());
            myReceiveOrderPojo.setOrderTime(genericMethod.getTimeDif(orderPojo.getReceiveOrderTime()));
            myReceiveOrderPojoArrayList.add(myReceiveOrderPojo);
        }

        mongoCursor.close();
        connMongoDB.getMongoClient().close();
        returnRes=gson.toJson(myReceiveOrderPojoArrayList,ArrayList.class);
        return returnRes;
    }
}
