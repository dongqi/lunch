package cn.eastseven.diancan.service.bo;

/**
 * Created by dongqi on 15/6/7.
 */
public interface CommonService {

    public static final String SEQ = "sequence";

    public long getSequenceNextValue(Class<?> clz);

}
