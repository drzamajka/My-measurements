package pl.kalisz.ak.rafal.peczek.mojepomiary.Dao;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateConverter {

    @TypeConverter
    public static Date toDate(Long dateLong){
        return dateLong == null ? null: new Date(dateLong);
    }

    @TypeConverter
    public static Long fromDate(Date date){
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static String fromList(ArrayList<Integer> list) { return list == null ? null : new Gson().toJson(list); }

    @TypeConverter
    public static ArrayList<Integer> toList(String list) { return list == null ? null: new Gson().fromJson(list, new TypeToken<ArrayList<Integer>>(){}.getType()); }
}