package com.heu.cs.dao.orderdao;

import com.google.gson.Gson;
import com.heu.cs.conndb.ConnMongoDB;
import com.heu.cs.utils.GenericMethod;
import com.heu.cs.utils.GenericMethodImpl;
import com.heu.cs.pojo.Order.MyPutOrderPojo;
import com.heu.cs.pojo.Order.OrderPojo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by memgq on 2017/6/5.
 */
public class QueryMyPutOrderDao {
    public String queryMyPutOrder(String orderOwnerId,String orderStatus){
        String returnRes="";
        ArrayList<MyPutOrderPojo> myPutOrderPojo=new ArrayList<MyPutOrderPojo>();
        Gson gson=new Gson();
        ConnMongoDB connMongoDB=new ConnMongoDB();
        MongoCollection collection=connMongoDB.getCollection("bbddb","normalorder");
        Document filter=new Document();
        if(orderStatus.equals("all")){
            filter.append("orderOwnerId",orderOwnerId);
        }else if(orderStatus.equals("0")){
            List<Document> list = new ArrayList<Document>();
            list.add(new Document("orderStatus","0"));
            list.add(new Document("orderStatus","1"));
            filter.append("orderOwnerId",orderOwnerId).append("$or",list);
        }else {
            filter.append("orderOwnerId",orderOwnerId).append("orderStatus",orderStatus);
        }

        Document sortDocument=new Document();
        sortDocument.append("putOrderTime",-1);
        MongoCursor<Document> mongoCursor= collection.find(filter).sort(sortDocument).limit(20).iterator();
        while (mongoCursor.hasNext()){
            Document d=mongoCursor.next();
            GenericMethod genericMethod =new GenericMethodImpl();
            genericMethod.updateOrderId(d,collection);
            OrderPojo orderPojo=gson.fromJson(d.toJson(),OrderPojo.class);
            MyPutOrderPojo myPOP=new MyPutOrderPojo();
            myPOP.setGoodsName(orderPojo.getGoodsName());
            myPOP.setCommit(orderPojo.getCommit());
            myPOP.setOrderId(d.get("_id").toString());
            myPOP.setOrderStatus(orderPojo.getOrderStatus());
            myPOP.setOrderTime(genericMethod.getTimeDif(orderPojo.getPutOrderTime()));
            myPutOrderPojo.add(myPOP);
        }

        mongoCursor.close();
        connMongoDB.getMongoClient().close();
        returnRes=gson.toJson(myPutOrderPojo,ArrayList.class);
        return returnRes;
    }



}
