package com.bytedance.club.interfaces;

import com.bytedance.club.entry.BookAutoPayTaskPackage;
import com.bytedance.club.entry.BookBuyMultiPackage;
import com.bytedance.club.entry.BookBuySinglePackage;
import com.bytedance.club.entry.BookChapterContentResult;
import com.bytedance.club.entry.BookChapterPackage;
import com.bytedance.club.entry.BookCommentListPackge;
import com.bytedance.club.entry.BookDetailInfoPackge;
import com.bytedance.club.entry.BookFreeCachePackage;
import com.bytedance.club.entry.BookModifyInfoPackage;
import com.bytedance.club.entry.BookMoreBuyInfoPackage;
import com.bytedance.club.entry.BookRecommendListPackage;
import com.bytedance.club.entry.UserAllTasksPackage;
import com.bytedance.club.entry.UserDiscountTaskRewardPackage;
import com.bytedance.club.entry.UserReadingTimeTaskRewardPackage;
import com.bytedance.club.entry.UserRecevieTaskRewardPackage;

import java.util.HashMap;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface ReaderApi {

    /**
     * 测试签名信息
     */
    @GET(InterFace.TEST_APP_SIGN)
    Single<ResponseBody> ReaderTestSign(@QueryMap HashMap<String, String> params);

    /**
     * 获取book书籍详情
     */
    @GET(InterFace.WORK_INFO)
    Single<BookDetailInfoPackge> ReaderBookInfo(@QueryMap HashMap<String, String> params);


    /**
     * 更新book书籍缓存信息 更新本地缓存
     */
    @GET(InterFace.WORK_MODIFY_INFO)
    Single<BookModifyInfoPackage> ReaderModifyBookInfo(@QueryMap HashMap<String, String> params);

    /**
     * 获取book书籍评论
     */
    @GET(InterFace.WORK_COMMENT_LIST)
    Single<BookCommentListPackge> ReaderBookCommentList(@QueryMap HashMap<String, String> params);


    /**
     * 获取book书籍 推荐列表
     */
    @GET(InterFace.READ_RECOMMEND)
    Single<BookRecommendListPackage> ReaderBookRecommendList(@QueryMap HashMap<String, String> params);


    /**
     * 获取book目录
     */
    @GET(InterFace.WORK_CATALOG)
    Single<BookChapterPackage> ReaderCatalog(@QueryMap HashMap<String, String> params);

    /**
     * 获取book章节内容
     */
    @GET(InterFace.WORK_CONTENT)
    Single<BookChapterContentResult> ReaderContent(@QueryMap HashMap<String, String> params);

    /**
     * 单章订阅
     */
    @GET(InterFace.SUBCRIPTION_SINGLE_CHAPTER)
    Single<BookBuySinglePackage> ReaderBuySingleContent(@QueryMap HashMap<String, String> params);

    /**
     * 批量购买页 内容信息
     */
    @GET(InterFace.PAY_MULTI_PAGE)
    Single<BookMoreBuyInfoPackage> ReaderMoreBuyInfo(@QueryMap HashMap<String, String> params);


    /**
     * 批量购买页 内容信息
     */
    @GET(InterFace.AUTO_TASk)
    Single<BookAutoPayTaskPackage> ReaderBookAutoTaskInfo(@QueryMap HashMap<String, String> params);


    /**
     * 多章订阅
     */
    @GET(InterFace.SUBCRIPTION_MULTI_CHAPTER)
    Single<BookBuyMultiPackage> ReaderBuyMultiContent(@QueryMap HashMap<String, String> params);

    /**
     * 免费下载章节信息
     */
    @GET(InterFace.FREE_DOWNLOAD_IDS)
    Single<BookFreeCachePackage> ReaderFreeCacheContent(@QueryMap HashMap<String, String> params);


    /**
     * 获取购买 任务奖励
     */
    @GET(InterFace.GET_DISCOUNT)
    Single<UserDiscountTaskRewardPackage> ReaderDiscountTaskReward(@QueryMap HashMap<String, String> params);


    /**
     * 获取全部任务列表
     */
    @GET(InterFace.GET_TASK)
    Single<UserAllTasksPackage> ReaderUserAllTasks(@QueryMap HashMap<String, String> params);

    /**
     * 领取任务奖励
     */
    @GET(InterFace.GET_TASK_REWARD)
    Single<UserRecevieTaskRewardPackage> ReaderUserRecevieTaskRewad(@QueryMap HashMap<String, String> params);




    /**
     * 上传阅读时长 任务奖励
     */
    @GET(InterFace.UPLOAD_READ_TIME)
    Single<UserReadingTimeTaskRewardPackage> ReaderUploadReadTimeTaskReward(@QueryMap HashMap<String, String> params);



//    /**
//     * 可缓存章节Id
//     */
//    String FREE_DOWNLOAD_IDS = "/index/work/free_down";
//
//    /**
//     * 单章订阅
//     */
//    String SUBCRIPTION_SINGLE_CHAPTER = "/index/consume/pay_single_cont";
//
//    /**
//     * 批量购买页
//     */
//    String PAY_MULTI_PAGE = "/index/consume/pay_multi_page_v2";
//
//    /**
//     * 批量订阅
//     */
//    String SUBCRIPTION_MULTI_CHAPTER = "/index/consume/pay_multi_cont_v2";
//
//
//    /**
//     * 单本订阅
//     */
//    String SUBCRIPTION_SINGLE_WORK = "/index/consume/pay_single_work";



    /**
     * 获取oss章节内容
     */
    @GET()
    Single<ResponseBody> ReaderOssContent(@Url String url);

}
