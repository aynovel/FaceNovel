package com.bytedance.club.weight;

import static com.bytedance.club.interfaces.BusC.BUS_NOTIFY_USER_READ_NEXT_CHAPTER;

import android.os.Message;

import com.bytedance.club.activtiy.PlotRead;
import com.bytedance.club.adapter.TxtChapter;
import com.bytedance.club.entry.BookBean;
import com.bytedance.club.entry.BookRecordBean;
import com.bytedance.club.entry.ChapterItemBean;
import com.bytedance.club.sql.DBUtils;
import com.bytedance.club.datautils.cache.ACache;
import com.bytedance.club.activtiy.AppUtils;
import com.bytedance.club.activtiy.BookManager;
import com.bytedance.club.activtiy.IOUtils;
import com.bytedance.club.activtiy.LogUtils;
import com.bytedance.club.activtiy.TimeUtils;
import com.bytedance.club.activtiy.ToastUtils;
import com.bytedance.club.interfaces.BusC;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;



public class NetPageLoader extends PageLoader {

    private int mStartTimeStamp = 0;

    /*****************************init params
     * @param pageView
     * @param collBook*******************************/
    public NetPageLoader(PageView pageView, BookBean collBook) {
        super(pageView, collBook);
    }

    private List<TxtChapter> convertTxtChapter(List<ChapterItemBean> bookChapters) {
        List<TxtChapter> txtChapters = new ArrayList<>(bookChapters.size());
        int i = 0;
        for (ChapterItemBean bean : bookChapters) {
            TxtChapter chapter = new TxtChapter();
            chapter.bookId = mCollBook.getWid();
            chapter.siteId = "0";

            chapter.title = bean.getChapterName();
            chapter.link = bean.link;
            chapter.chapterId = bean.getChapterId();
            chapter.chapterOrder = i;
            txtChapters.add(chapter);
            i++;
        }
        return txtChapters;
    }

    @Override
    public void refreshChapterList() {
        if (mCollBook.getBookChapterList() == null) return;

        // ??? BookChapter ???????????????????????? Chapter
        mChapterList = convertTxtChapter(mCollBook.getBookChapterList());
        isChapterListPrepare = true;

        // ??????????????????????????????????????????
        if (mPageChangeListener != null) {
            mPageChangeListener.onCategoryFinish(mChapterList);
        }

        // ?????????????????????
        if (!isChapterOpen()) {
            // ????????????
            mStartTimeStamp = TimeUtils.getCurrentTimestamp();

            openChapter();
        }

    }

    @Override
    protected BufferedReader getChapterReader(TxtChapter chapter) throws Exception {
        String siteID = "0";
//        if(mCollBook.isYueWenBook())
//        {
//            if(mCollBook.getCurrentSiteBean() != null)
//            {
//                siteID =  mCollBook.getCurrentSiteBean().id;
//            }
//        }else{
//
//        }
        // TODO:1.8.1 2021/9/29 1.8.1 ????????????11111
        if(!realHasChapterData(chapter))
        {
            String tempContentStr =  "Loading ......\n";

            if(chapter.getmChatperStatusInfo() != null)
            {

                if(chapter.getmChatperStatusInfo().getChapterContentStr() != null)
                {
                    switch (chapter.getmChatperStatusInfo().getMode())
                    {
                        case ERROR:
                        {
                            tempContentStr = "      ";
                        }
                        break;
                        default:
                        {
                            if(chapter.getmChatperStatusInfo().getChapterContentStr() != null)
                            {
                                tempContentStr = chapter.getmChatperStatusInfo().getChapterContentStr();
                            }
                        }
                        break;
                    }

                }else{
                    if(chapter.getmChatperStatusInfo().getMode() == ChapterPageStatusInfo.PageStatusMode.ERROR)
                    {
                        tempContentStr = "      ";
                    }else{
//                        chapter.getmChatperStatusInfo().setMode(ChapterPageStatusInfo.PageStatusMode.LOADING);

                        if(mPageChangeListener != null && mNativeAdListener != null && mNativeAdListener.isFroceClearStatusContent())
                        {
                            List<TxtChapter> tempChapters = new ArrayList<>();
                            tempChapters.add(chapter);
                            mPageChangeListener.requestChapters(tempChapters);
                        }
                    }
                }
            }
            BufferedReader tempBr = IOUtils.StringToBufferedReader(tempContentStr);

            return tempBr;
        }



        File file = BookManager.getBookFile(mCollBook.getWid(), siteID, chapter.getChapterId());
        if (!file.exists()) {

        }

        try {
            Reader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            return br;
        }catch (Exception e){
            if(AppUtils.is_AppCheck)
            {
                if(ACache.get(PlotRead.getContext()).getAsString(chapter.getTitle()) !=null)
                {
                    String contentStr =  ACache.get(PlotRead.getContext()).getAsString(chapter.getTitle());
                    BufferedReader br = IOUtils.StringToBufferedReader(contentStr);
                    return br;
                }
            }else{
                ToastUtils.show(e.toString());

                Message message = Message.obtain();
                message.what = BusC.BUS_NOTIFY_REQUEST_STROGE;
                EventBus.getDefault().post(message);

                return null;
            }
        }

        return null;

    }


    @Override
    protected boolean hasChapterData(TxtChapter chapter) {
//        String siteID = "0";
//        if(mCollBook.isYueWenBook())
//        {
//            if(mCollBook.getCurrentSiteBean() != null)
//            {
//                siteID = mCollBook.getCurrentSiteBean().id;
//            }
//        }
//
//        return BookManager.isChapterCached(mCollBook.wid, siteID, chapter.title);

        return true;
    }

    @Override
    protected  boolean realHasChapterData(TxtChapter chapter){


        return BookManager.isChapterCached(mCollBook.wid, "0", chapter.getChapterId());

//        return false;
    }



    // ???????????????????????????
    @Override
    boolean parsePrevChapter() {
        boolean isRight = super.parsePrevChapter();

//        if (mStatus == STATUS_FINISH) {
            loadPrevChapter();
//        } else if (mStatus == STATUS_LOADING) {
//            loadCurrentChapter();
//        }
        return isRight;
    }

    // ????????????????????????
    @Override
    boolean parseCurChapter() {
        boolean isRight = super.parseCurChapter();



//        if (mStatus == STATUS_LOADING) {
//            loadCurrentChapter();
//        }
        loadCurrentChapter();


        return isRight;
    }

    // ???????????????????????????
    @Override
    boolean parseNextChapter() {
        boolean isRight = super.parseNextChapter();

//        if (mStatus == STATUS_FINISH) {
            loadNextChapter();
//        } else if (mStatus == STATUS_LOADING) {
//            loadCurrentChapter();
//        }

        Message message = Message.obtain();
        message.what = BUS_NOTIFY_USER_READ_NEXT_CHAPTER;
        EventBus.getDefault().post(message);


        return isRight;
    }

    /**
     * ????????????????????????????????????
     */
    private void loadPrevChapter() {
        if (mPageChangeListener != null) {
            int end = mCurChapterPos;
            int begin = end - 2;
            if (begin < 0) {
                begin = 0;
            }

            requestChapters(begin, end);
        }
    }

    /**
     * ??????????????????????????????????????????
     */
    private void loadCurrentChapter() {
        if (mPageChangeListener != null) {
            int begin = mCurChapterPos;
            int end = mCurChapterPos;

            // ??????????????????????????????
            if (end < mChapterList.size()) {
                end = end + 1;
                if (end >= mChapterList.size()) {
                    end = mChapterList.size() - 1;
                }
            }

            // ???????????????????????????
            if (begin != 0) {
                begin = begin - 1;
                if (begin < 0) {
                    begin = 0;
                }
            }

            requestChapters(begin, end);
        }
    }

    /**
     * ?????????????????????????????????
     */
    private void loadNextChapter() {
        if (mPageChangeListener != null) {

            // ?????????????????????
            int begin = mCurChapterPos + 1;
            int end = begin + 1;

            // ??????????????????????????????
            if (begin >= mChapterList.size()) {
                // ?????????????????????????????????????????????????????????
                return;
            }

            if (end > mChapterList.size()) {
                end = mChapterList.size() - 1;
            }

            requestChapters(begin, end);
        }
    }

    private void requestChapters(int start, int end) {
        // ???????????????
        if (start < 0) {
            start = 0;
        }

        if (end >= mChapterList.size()) {
            end = mChapterList.size() - 1;
        }


        List<TxtChapter> chapters = new ArrayList<>();

        // ????????????????????????????????????
        for (int i = start; i <= end; ++i) {
            TxtChapter txtChapter = mChapterList.get(i);


//            if (!hasChapterData(txtChapter)) {
//                chapters.add(txtChapter);
//            }


            if (!realHasChapterData(txtChapter)) {

                if(txtChapter.getmChatperStatusInfo() != null)
                {
                    //txtChapter.getmChatperStatusInfo().getChapterContentStr() != null

                    // TODO: 2021/10/9  1.8.1 ????????????????????????
                    if(txtChapter.getmChatperStatusInfo().getMode() == ChapterPageStatusInfo.PageStatusMode.ERROR)
                    {

                    }else{
                        if(txtChapter.getmChatperStatusInfo().getChapterContentStr() == null)
                        {
                            chapters.add(txtChapter);
                        }else{

                        }
                    }

                    if(mNativeAdListener != null &&
                            mNativeAdListener.isFroceClearStatusContent() && txtChapter.errorTimes < 3)
                    {
                        if(txtChapter.getmChatperStatusInfo().getMode() == ChapterPageStatusInfo.PageStatusMode.PAY)
                        {
                            String tempContentStr =  "Loading ......\n";
                            txtChapter.getmChatperStatusInfo().setChapterContentStr(tempContentStr);
                            txtChapter.getmChatperStatusInfo().setMode(ChapterPageStatusInfo.PageStatusMode.LOADING);
                            chapters.add(txtChapter);
                        }
                    }

                }else{
                    chapters.add(txtChapter);
                }
            }
        }

        if (!chapters.isEmpty()) {
            mPageChangeListener.requestChapters(chapters);
        }
    }

    @Override
    public void saveRecord() {
        super.saveRecord();
        if (mCollBook != null && isChapterListPrepare) {
            //????????????CollBook????????????
            mCollBook.setIs_update(false);


//            if (DeviceDataManager.getInstance().shouldShelfReverse()){
//                mCollBook.setLastRead(NStringUtils.
//                    dateConvert(System.currentTimeMillis(), Constant.FORMAT_BOOK_DATE));
//            }
//            BookManager.saveBookrecord(mCollBook.get_bid());
//            //????????????
//            BookRepository.getInstance()
//                    .saveCollBook(mCollBook);
//
//

            BookRecordBean recordBean = new BookRecordBean();
            recordBean.wid = mCollBook.wid;
            recordBean.title = mCollBook.title;
            recordBean.h_url = mCollBook.h_url;
            recordBean.chapterCount = mCollBook.chapterCount;
            recordBean.author = mCollBook.author;
            recordBean.cp_name = mCollBook.cp_name;
            recordBean.is_finish = mCollBook.is_finish;
            recordBean.status = mCollBook.status;

            LogUtils.d("???????????? ===== "+ getChapterPos() + " ==== "+ getCurPageStartCharPos());
            recordBean.chapterIndex = getChapterPos();
            recordBean.chapterCharIndex = getCurPageStartCharPos();

            if(mCurPage != null)
            {
                String chapterName = mCurPage.title;
                if(chapterName != null)
                {
                    recordBean.chapterName = chapterName;
                }
            }
            if(mCollBook.getBookChapterList() == null)
            {
                return;
            }

            if(mCollBook.getBookChapterList().size() <= 0)
            {
                return;
            }

            if(mCollBook.getBookChapterList().size()<getChapterPos())
            {
                return;
            }

            recordBean.chapterID = mCollBook.getBookChapterList().get(getChapterPos()).getChapterId();
            recordBean.timeStamp = TimeUtils.getCurrentTimestamp();
            recordBean.readtime = TimeUtils.getCurrentTimestamp();
            recordBean.cp_name = mCollBook.cp_name;


            DBUtils.getInstance().saveBookRecordWithAsync(recordBean);

//            BookBean bookShelfBean = BookBean.getBookShelfGreendao(mCollBook);
//            if(bookShelfBean != null)
//            {
//                bookShelfBean.chapterCount = mCollBook.chapterCount;
//                bookShelfBean.chapterindex = recordBean.chapterIndex;
//                bookShelfBean.lastchapterpos = recordBean.chapterCharIndex;
//                bookShelfBean.chapterid = recordBean.chapterID;
//                bookShelfBean.readtime = TimeUtils.getCurrentTimestamp();
//                bookShelfBean.cp_name = mCollBook.cp_name;
//                if(bookShelfBean.cp == 0)
//                {
//                    bookShelfBean.cp = mCollBook.cp;
//                }
//
//                BookBean.saveBookShelfGreenDao(bookShelfBean);
//            }

            int readTime = TimeUtils.getCurrentTimestamp() - mStartTimeStamp;

//            BookReadTimeBean timeBean = new BookReadTimeBean();
//            timeBean.bookID = mCollBook.wid;
//            timeBean.chapterId = recordBean.chapterID;
//            timeBean.readperiod = readTime;
//            timeBean.timeStamp = TimeUtils.getCurrentTimestamp();
//            BookReadTimeBean.saveBookReadTimeBeanDao(timeBean);
//
//
//            BookReadTimeBean bookReadTimeBean = BookReadTimeBean.getBookReadTimeBean(mCollBook.wid);
//            JuYueCloudBigDataBean bigDataBean = new JuYueCloudBigDataBean();
//            bigDataBean.setCloudBigDataEvent(BIGDATA_EVENT_ID_READING_BOOK,
//                    mCollBook.wid,
//                    recordBean.chapterID,
//                    mCollBook.rec_id,
//                    bookReadTimeBean.readperiod
//            );
        }
    }
}

