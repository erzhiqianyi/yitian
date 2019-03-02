package io.github.buniaowanfeng.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.github.buniaowanfeng.R;
import io.github.buniaowanfeng.YiTian;
import io.github.buniaowanfeng.todo.data.model.DateInfo;
import io.github.buniaowanfeng.todo.data.model.Tag;
import io.github.buniaowanfeng.todo.data.model.Tags;
import io.github.buniaowanfeng.todo.data.model.Todo;
import io.github.buniaowanfeng.todo.data.model.TodoBean;
import io.github.buniaowanfeng.todo.data.model.TodoEmpty;
import io.github.buniaowanfeng.todo.data.model.UserInfo;

/**
 * Created by caofeng on 16-9-20.
 */
public class InitUtil {
    public static List<Tag> makeTag() {
        ArrayList<Tag> tags = new ArrayList<>();
        Tags tag = new Tags();
        Tag work = new Tag();
        work.id = 2;
        work.value = YiTian.mContext.getString(R.string.work);
        tags.add(work);

        Tag sleep = new Tag();
        sleep.id = 3;
        sleep.value = YiTian.mContext.getString(R.string.sleep);
        tags.add(sleep);

        Tag custom = new Tag();
        custom.id = 1;
        custom.value = YiTian.mContext.getString(R.string.custom);
        tags.add(custom);

        tag.tags = tags;
        SpUtil.getInstance().putString(SpUtil.KEY_TAG,Tags.toJson(tag));
        return tags;
    }

    public static List<Tag> makeLocation() {
        ArrayList<Tag> tags = new ArrayList<>();
        Tags tag = new Tags();

        Tag custom = new Tag();
        custom.id = 0;
        custom.value = YiTian.mContext.getString(R.string.custom);
        tags.add(custom);
        tag.tags = tags;
        SpUtil.getInstance().putString(SpUtil.KEY_LOCATION,Tags.toJson(tag));
        return tags;
    }

    public static void  makeTodoBean(TodoBean bean) {
        String[] week = {"周日","周一","周二","周三","周四","周五","周六"};
        int[] img = {R.drawable.ic_timeofday_morning,
                R.drawable.ic_timeofday_afternoon
                ,R.drawable.ic_timeofday_night};
        Date date = new Date(System.currentTimeMillis());

        DateInfo dateInfo = new DateInfo();
        dateInfo.year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
        dateInfo.month = Integer.parseInt(new SimpleDateFormat("MM").format(date));
        dateInfo.day = date.getDate();
        dateInfo.date = week[date.getDay()];

        TodoEmpty empty = new TodoEmpty();
        empty.startHour = empty.endHour = date.getHours();
        empty.startMinute = empty.endMinute = date.getMinutes();
        int srcId ;
        if (empty.startHour < 12 && empty.startHour > 6){
            srcId = 0 ;
        }else if (empty.startHour < 18){
            srcId = 1;
        }else {
            srcId = 2;
        }

        Todo data = new Todo();
        data.canChangeTime = 1;
        UserInfo userInfo = new UserInfo();
        userInfo.userId = SpUtil.getInstance().getInt(SpUtil.KEY_USER_ID);
        empty.iconId = img[srcId];

        bean.dateInfo = dateInfo;
        bean.empty = empty;
        bean.data = data;
        bean.userInfo = userInfo;

    }
}
