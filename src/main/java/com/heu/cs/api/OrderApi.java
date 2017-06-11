package com.heu.cs.api;


import com.google.gson.Gson;
import com.heu.cs.dao.orderdao.*;
import com.heu.cs.pojo.ReturnInfoPojo;
import com.heu.cs.service.image.ImageClient;
import com.heu.cs.utils.TencentYouTu;
import com.heu.cs.utils.TencentYouTuImpl;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by memgq on 2017/5/14.
 */
@Path("/order")
public class OrderApi {

    private static final int appId = 1252947691;//      YOUR_APPID
    private static final String secretId = "AKIDgGajkbKhx64pQpF5xc5MQIyCIG6nRgst";
    private static final String secretKey = "0qdHl9YpvrcCbeZJsLdjdLtFQPDpFW4S";
    private static final String HOST="http://mengqipoet.cn:8080/upload_images/";
    // ImageClient


    // 设置要操作的bucket
    private static final String bucketName = "helpsendv1";


    @Context
    UriInfo uriInfo;

    @Context
    HttpServletRequest request;


    /**
     * Constants operating with images
     */
    private static final String ROOTPATH = System.getProperty("user.dir");
    private static final String ROOT_IMAGES_PATH = "/src/main/resources";
    private static final String JPG_CONTENT_TYPE = "image/jpeg";
    private static final String PNG_CONTENT_TYPE = "image/png";
    private static final String IMAGE_URL = "/upload_images/";


    @GET
    @Produces({"text/html"})
    public String index() {
        return "OK";
    }


    /**
     * 接单，通过订单ID和接单人ID来确定接单
     *
     * @param orderId
     * @param orderReceiverId
     * @return
     */
    @GET
    @Path("/receiveorder")
//    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("text/plain;charset=utf-8")
    public String receiveOrderURL(@QueryParam("orderId") String orderId,
                                  @QueryParam("orderReceiverId") String orderReceiverId) {
        ReceiveOrderDao receiveOrderDao = new ReceiveOrderDao();
        String result = receiveOrderDao.receiveOrder(orderId, orderReceiverId);
        return result;
    }


    /**
     * 下单，可选择是否上传图片
     *
     * @param fileInputStream
     * @param disposition
     * @param orderInfoStr
     * @return
     */
    @POST
    @Path("/createorder")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("text/plain")
    public String cteateOrderURL(@FormDataParam("photos") InputStream fileInputStream,
                                 @FormDataParam("photos") FormDataContentDisposition disposition,
                                 @FormDataParam("orderinfo") String orderInfoStr) {
        ReturnInfoPojo returnInfo = new ReturnInfoPojo();
        String status = "";
        CreateOrderDao createOrderDao = new CreateOrderDao();
        String imageName = disposition.getFileName();
        Gson gson = new Gson();
        if (!imageName.equals("")) {
            imageName = Calendar.getInstance().getTimeInMillis() + imageName;
            String imgUrl = ROOTPATH + ROOT_IMAGES_PATH + IMAGE_URL + imageName;
            String imgHostUrl=HOST+imageName;
            File file = new File(imgUrl);
            try {
                //使用common io的文件写入操作
                FileUtils.copyInputStreamToFile(fileInputStream, file);
                TencentYouTu tencentYouTu = new TencentYouTuImpl();
                ImageClient imageClient = new ImageClient(appId, secretId, secretKey);
                String pornRes = tencentYouTu.detectPorn(imgHostUrl, imageClient, bucketName);
                System.out.println("检测结果："+pornRes);
                imageClient.shutdown();
                if (pornRes.equals("0")) {
                    status = createOrderDao.insertOrder(orderInfoStr, IMAGE_URL + imageName);
                    String s="";
                    String m="";
                    if (status.equals("1")) {
                        s="1";
                        m="下单成功";
                    } else {
                        s="0";
                        m="下单失败";
                    }
                    returnInfo.setStatus(s);
                    returnInfo.setMessage(m);
                } else {
                    returnInfo.setStatus("0");
                    returnInfo.setMessage("图片不合格");
                }
                return gson.toJson(returnInfo, ReturnInfoPojo.class);

            } catch (IOException ex) {
                Logger.getLogger(UploadFileApi.class.getName()).log(Level.SEVERE, null, ex);
                returnInfo.setStatus("0");
                returnInfo.setMessage("文件上传出错");
                return gson.toJson(returnInfo, ReturnInfoPojo.class);
            }

        } else {
            status = createOrderDao.insertOrder(orderInfoStr, IMAGE_URL);
            if (status.equals("1")) {
                returnInfo.setStatus(status);
                returnInfo.setMessage("下单成功");
            } else {
                returnInfo.setStatus("0");
                returnInfo.setMessage("下单失败");
            }
            return gson.toJson(returnInfo, ReturnInfoPojo.class);
        }
    }


    /**
     * 通过状态码来查询自己的不同状态的订单
     *
     * @param orderOwnerId
     * @param orderStatus  0:已下单 1:已接单 2:已完成 -1:已取消
     * @return
     */
    @GET
    @Path("/queryselforderbystatus")
    @Produces("text/plain;charset=utf-8")
    public String queryOrderByURL(@QueryParam("orderOwnerId") String orderOwnerId,
                                  @QueryParam("orderStatus") String orderStatus) {
        QuerySelfOrderByStatusDao querySelfOrderByStatusDao = new QuerySelfOrderByStatusDao();
        String result = querySelfOrderByStatusDao.queryNewOrder(orderOwnerId, orderStatus);
        return result;
    }

    /**
     * 查询自己发出的所有订单
     *
     * @param orderOwnerId
     * @return
     */
    @GET
    @Path("/queryselfallorder")
    @Produces("text/plain;charset=utf-8")
    public String queryNewOrderByStatusURL(@QueryParam("orderOwnerId") String orderOwnerId) {
        QuerySelfAllOrderDao querySelfAllOrderDao = new QuerySelfAllOrderDao();
        String result = querySelfAllOrderDao.querySelfAllOrder(orderOwnerId);
        return result;
    }

    /**
     * 根据订单Id取消订单
     *
     * @param orderId
     * @return
     */
    @GET
    @Path("/cancelorder")
    @Produces("text/plain;charset=utf-8")
    public String cancelOrderURL(@QueryParam("orderId") String orderId) {
        CancelOrderDao cancelOrderDao = new CancelOrderDao();
        String result = cancelOrderDao.cancelOrder(orderId);
        return result;
    }


    /**
     * 抢单，查询附近(NEARBY)一定距离内所有的没人接的订单，并按照期望配送员取物品时间降序排列
     *
     * @return
     */
    @GET
    @Path("/graborder")
    @Produces("text/plain;charset=utf-8")
    public String grabOrderURL(@QueryParam("latitude") String latitude, @QueryParam("longitude") String longitude) throws ParseException {
        GrabOrderDao grabOrderDao = new GrabOrderDao();
        String result = grabOrderDao.grabOrder(latitude, longitude);
        return result;
    }

    /**
     * 根据订单Id查询订单详情
     *
     * @param orderId
     * @return
     */
    @GET
    @Path("/graborderdetails")
    @Produces("text/plain;charset=utf-8")
    public String grabOrderDetailsURL(@QueryParam("orderId") String orderId) {
        GrabOrderDetailsDao grabOrderDetailsDao = new GrabOrderDetailsDao();
        String result = grabOrderDetailsDao.grabOrderDetails(orderId);
        return result;
    }


    @GET
    @Path("/querymyputorder")
    @Produces("text/plain;charset=utf-8")
    public String queryMyPlaceOrderURL(@QueryParam("orderOwnerId") String orderOwnerId,
                                       @QueryParam("orderStatus") String orderStatus) {
        QueryMyPutOrderDao queryMyPutOrderDao = new QueryMyPutOrderDao();
        String result = queryMyPutOrderDao.queryMyPutOrder(orderOwnerId, orderStatus);
        return result;
    }


    @GET
    @Path("/querymyreceiveorder")
    @Produces("text/plain;charset=utf-8")
    public String queryMyReceiveOrderURL(@QueryParam("orderReceiverId") String orderReceiverId,
                                         @QueryParam("orderStatus") String orderStatus) {
        QueryMyReceiveOrderDao queryMyReceiveOrderDao = new QueryMyReceiveOrderDao();
        String result = queryMyReceiveOrderDao.queryMyReceiveOrder(orderReceiverId, orderStatus);
        return result;
    }




    @GET
    @Path("/queryorderprogress")
    @Produces("text/plain;charset=utf-8")
    public String queryOrderProgressURL(@QueryParam("orderId") String orderId) {
        QueryOrderProgressDao queryOrderProgressDao = new QueryOrderProgressDao();
        String result = queryOrderProgressDao.queryOrderProgress(orderId);
        return result;
    }


    @GET
    @Path("/deliveryorder")
    @Produces("text/plain;charset=utf-8")
    public String deliveryOrderURL(@QueryParam("orderId") String orderId) throws IOException {
        DeliveryOrderDao deliveryOrderDao = new DeliveryOrderDao();
        String result = deliveryOrderDao.deliveryOrder(orderId);
        return result;
    }


    @GET
    @Path("/getprice")
    @Produces("text/plain;charset=utf-8")
    public String getPriceURL(@QueryParam("location") String location) throws IOException {
        GetPriceDao getPriceDao = new GetPriceDao();
        return getPriceDao.getPrice(location);
    }




    @GET
    @Path("/commitorder")
    @Produces("text/plain;charset=utf-8")
    public String commitOrderURL(@QueryParam("commit") double commit,@QueryParam("orderId") String orderId) throws IOException {
        CommitOrderDao commitOrderDao=new CommitOrderDao();
        return commitOrderDao.commitOrder(commit,orderId);
    }

}
