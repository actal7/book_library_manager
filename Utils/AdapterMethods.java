package Utils;

import Templates.SectionEnum;

import java.util.ArrayList;
import java.util.List;

public class AdapterMethods {
    public static List<SectionEnum> stringListToSectionEnumList(List<String> stringList) {
        List<SectionEnum> sectionEnumList = new ArrayList<>();
        for (String section : stringList) {
            sectionEnumList.add(SectionEnum.valueOf(section));
        }
        return sectionEnumList;
    }

    public static List<String> sectionEnumListToStringList(List<SectionEnum> sectionEnumList) {
        List<String> stringList = new ArrayList<>();
        for (SectionEnum section : sectionEnumList) {
            stringList.add(section.toString());
        }
        return stringList;
    }

    // write a function which takes a list of enums and returns a single string which represents the enums
    // e.g. [SectionEnum.ART, SectionEnum.FANTASY, SectionEnum.MYSTERY] -> "ART, FANTASY, MYSTERY"
    public static String sectionEnumListToString(List<SectionEnum> sectionEnumList) {
        String string = "";
        for (SectionEnum section : sectionEnumList) {
            string += section.toString() + ", ";
        }
        return string.substring(0, string.length() - 2);
    }
}

