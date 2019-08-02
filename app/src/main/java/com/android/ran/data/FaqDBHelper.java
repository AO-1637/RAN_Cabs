package com.android.ran.data;

import android.content.Context;

import com.android.ran.R;
import com.android.ran.model.Faq;

import java.util.ArrayList;
import java.util.List;

public class FaqDBHelper {

    private static FaqDBHelper fd;
    private static List<Faq> mFaqList = new ArrayList<>();

    private FaqDBHelper(Context context) {
        mFaqList.add(new Faq(context.getString(R.string.faqs_question_1), context.getString(R.string.faqs_answer_1)));
        mFaqList.add(new Faq(context.getString(R.string.faqs_question_2), context.getString(R.string.faqs_answer_2)));
        mFaqList.add(new Faq(context.getString(R.string.faqs_question_3), context.getString(R.string.faqs_answer_3)));
        mFaqList.add(new Faq(context.getString(R.string.faqs_question_4), context.getString(R.string.faqs_answer_4)));
    }

    public static List<Faq> getFaqDataList(Context context) {
        if (fd == null)
            fd = new FaqDBHelper(context);

        return mFaqList;
    }
}