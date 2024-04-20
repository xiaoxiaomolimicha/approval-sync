package com.erplus.sync.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Constants {


    //无需同步的组件类型
    public static Set<Integer> noNeedSyncTypes = Stream.of(ContentType.ARTICLE, ContentType.ATTACHMENT, ContentType.COMPONENT_GROUP, ContentType.NEW_ADD_CLOCK_CARD, ContentType.TIME_RANGE)
            .collect(Collectors.toSet());
    public static Set<Integer> nameListTypes = Stream.of(ContentType.USER, ContentType.DEPARTMENT).collect(Collectors.toSet());
    public static Set<Integer> objectTitleTypes = Stream.of(ContentType.RELATED_TASK, ContentType.RELATED_APPROVAL, ContentType.RELATED_CLIENT, ContentType.RELATE_SUPPLIER,
            ContentType.PRODUCT, ContentType.PROGRAM, ContentType.TALENT, ContentType.BUSINESS_OPPORTUNITY, ContentType.CRM_CONTACT, ContentType.RELATE_ORDER,
            ContentType.RELATE_PURCHASE, ContentType.OUT_STORE).collect(Collectors.toSet());

    public static List<Integer> handleCompanyIds = Arrays.asList(12, 1829, 10824, 11099, 11086, 11075, 11072, 11063, 11059, 11010, 11002, 10977, 10961, 10960, 10930, 10916, 10900, 10889, 10888, 10873, 10867, 10862, 10854, 10851,
            10843, 10824, 10813, 10798, 10791, 10787, 10772, 10766, 10758, 10756, 10753, 10728, 10702, 10696, 10693, 10682, 10668, 10657, 10654, 10641, 10622, 10618, 10614, 10589, 10536, 10532, 10559, 10612, 10685, 10703, 10796,
            10880, 10899, 10918, 10949, 10490, 10486, 10484, 10481, 10476, 10458, 10452, 10449, 10445, 10444, 10441, 10438, 10425, 10420, 10419, 10403, 10397, 10383, 10379, 10372, 10370, 10368, 10367, 10362, 10361, 10340, 10337,
            10336, 10324, 10321, 10318, 10305, 10301, 10266, 10007, 10004, 10126, 10063, 10242, 10071, 10035, 10112, 10051, 10289, 10172, 10083, 10281, 10259, 10146, 10298, 10055, 10245, 10025, 10231, 10290, 10066, 10274, 10080,
            10093, 10043, 10204, 10053, 10267, 7949, 9030, 9082, 9285, 9571, 9684, 9128, 63, 353, 802, 1888, 1992, 1996, 3171, 3176, 4615, 5027, 5028, 5184, 5432, 5847, 6421, 6423, 6444, 7005, 8176, 8688, 9950, 9988, 9889, 9580,
            9966, 9773, 9538, 9729, 9899, 9664, 9967, 9943, 9616, 9524, 9982, 9600, 9559, 9904, 9736, 9581, 9868, 9641, 9955, 9519, 9732, 9709, 9500,9491,9453,9444,9423,9396,9380,9371,9361,9359,9358,9316,9315,9309,9308,9302,9247,
            9214,9182,9179,9167,9161,9151,9145,9144,9112,9090,9056,9035,9017);






}
