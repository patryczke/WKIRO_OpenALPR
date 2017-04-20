package pl.polsl.wkiro.recognizer.utils;

import java.util.*;

public class Dictionary {

    public interface Regions {
        String AUSTRALIA = "au";
        String EUROPE = "eu";
        String GREAT_BRITAIN = "gb";
        String SOUTH_KOREA = "kr";
        String MEXICO = "mx";
        String SINGAPORE = "sg";
        String USA = "us";
    }

    public static final String NOT_SELECTED = "-";

    private static List<String> REGIONS;
    static {
        List<String> regions = new ArrayList<>();
        regions.add(Regions.AUSTRALIA);
        regions.add(Regions.EUROPE);
        regions.add(Regions.GREAT_BRITAIN);
        regions.add(Regions.SOUTH_KOREA);
        regions.add(Regions.MEXICO);
        regions.add(Regions.SINGAPORE);
        regions.add(Regions.USA);
        REGIONS = Collections.unmodifiableList(regions);
    }

    private static Map<String, List<String>> SPECIFIC_REGIONS;
    static {
        Map<String, List<String>> specificRegionMap = new HashMap<>();

        List<String> specificRegion = new ArrayList<>();
        specificRegion.add(NOT_SELECTED);
        specificRegion.add("act");
        specificRegion.add("nsw");
        specificRegion.add("nt");
        specificRegion.add("qld");
        specificRegion.add("sa");
        specificRegion.add("tas");
        specificRegion.add("vic");
        specificRegion.add("wa");
        Collections.sort(specificRegion);
        specificRegionMap.put(Regions.AUSTRALIA, Collections.unmodifiableList(specificRegion));

        specificRegion = new ArrayList<>();
        specificRegion.add(NOT_SELECTED);
        specificRegion.add("ad");
        specificRegion.add("al");
        specificRegion.add("am");
        specificRegion.add("at");
        specificRegion.add("az");
        specificRegion.add("ba");
        specificRegion.add("be");
        specificRegion.add("bg");
        specificRegion.add("by");
        specificRegion.add("ch");
        specificRegion.add("cy");
        specificRegion.add("cz");
        specificRegion.add("de");
        specificRegion.add("dk");
        specificRegion.add("ee");
        specificRegion.add("es");
        specificRegion.add("fi");
        specificRegion.add("fr");
        specificRegion.add("gb");
        specificRegion.add("ge");
        specificRegion.add("gi");
        specificRegion.add("gr");
        specificRegion.add("hr");
        specificRegion.add("hu");
        specificRegion.add("ie");
        specificRegion.add("is");
        specificRegion.add("it");
        specificRegion.add("kz");
        specificRegion.add("li");
        specificRegion.add("lt");
        specificRegion.add("lu");
        specificRegion.add("lv");
        specificRegion.add("mc");
        specificRegion.add("md");
        specificRegion.add("me");
        specificRegion.add("mk");
        specificRegion.add("mt");
        specificRegion.add("nl");
        specificRegion.add("no");
        specificRegion.add("pl");
        specificRegion.add("pt");
        specificRegion.add("ro");
        specificRegion.add("rs");
        specificRegion.add("ru");
        specificRegion.add("se");
        specificRegion.add("si");
        specificRegion.add("sk");
        specificRegion.add("sm");
        specificRegion.add("tr");
        specificRegion.add("ua");
        specificRegion.add("va");
        Collections.sort(specificRegion);
        specificRegionMap.put(Regions.EUROPE, Collections.unmodifiableList(specificRegion));

        specificRegion = new ArrayList<>();
        specificRegion.add(NOT_SELECTED);
        specificRegion.add("gb");
        Collections.sort(specificRegion);
        specificRegionMap.put(Regions.GREAT_BRITAIN, Collections.unmodifiableList(specificRegion));

        specificRegion = new ArrayList<>();
        specificRegion.add(NOT_SELECTED);
        specificRegion.add("kr");
        Collections.sort(specificRegion);
        specificRegionMap.put(Regions.SOUTH_KOREA, Collections.unmodifiableList(specificRegion));

        specificRegion = new ArrayList<>();
        specificRegion.add(NOT_SELECTED);
        specificRegion.add("mx");
        Collections.sort(specificRegion);
        specificRegionMap.put(Regions.MEXICO, Collections.unmodifiableList(specificRegion));

        specificRegion = new ArrayList<>();
        specificRegion.add(NOT_SELECTED);
        specificRegion.add("sg");
        Collections.sort(specificRegion);
        specificRegionMap.put(Regions.SINGAPORE, Collections.unmodifiableList(specificRegion));

        specificRegion = new ArrayList<>();
        specificRegion.add(NOT_SELECTED);
        specificRegion.add("base");
        specificRegion.add("al");
        specificRegion.add("ak");
        specificRegion.add("as");
        specificRegion.add("az");
        specificRegion.add("ar");
        specificRegion.add("ca");
        specificRegion.add("co");
        specificRegion.add("ct");
        specificRegion.add("de");
        specificRegion.add("dc");
        specificRegion.add("fl");
        specificRegion.add("ga");
        specificRegion.add("gu");
        specificRegion.add("hi");
        specificRegion.add("id");
        specificRegion.add("il");
        specificRegion.add("in");
        specificRegion.add("ia");
        specificRegion.add("ks");
        specificRegion.add("ky");
        specificRegion.add("la");
        specificRegion.add("me");
        specificRegion.add("ms");
        specificRegion.add("md");
        specificRegion.add("ma");
        specificRegion.add("mi");
        specificRegion.add("mn");
        specificRegion.add("mo");
        specificRegion.add("mt");
        specificRegion.add("ne");
        specificRegion.add("nv");
        specificRegion.add("nh");
        specificRegion.add("nj");
        specificRegion.add("nm");
        specificRegion.add("ny");
        specificRegion.add("nc");
        specificRegion.add("nd");
        specificRegion.add("mp");
        specificRegion.add("oh");
        specificRegion.add("ok");
        specificRegion.add("or");
        specificRegion.add("pa");
        specificRegion.add("pr");
        specificRegion.add("ri");
        specificRegion.add("sc");
        specificRegion.add("sd");
        specificRegion.add("tn");
        specificRegion.add("tx");
        specificRegion.add("ut");
        specificRegion.add("vt");
        specificRegion.add("vi");
        specificRegion.add("va");
        specificRegion.add("wa");
        specificRegion.add("wi");
        specificRegion.add("wv");
        specificRegion.add("wy");
        specificRegion.add("ab");
        specificRegion.add("bc");
        specificRegion.add("mb");
        specificRegion.add("nl");
        specificRegion.add("ns");
        specificRegion.add("nt");
        specificRegion.add("nu");
        specificRegion.add("on");
        specificRegion.add("pe");
        specificRegion.add("qc");
        specificRegion.add("sk");
        specificRegion.add("yt");
        Collections.sort(specificRegion);
        specificRegionMap.put(Regions.USA, Collections.unmodifiableList(specificRegion));

        SPECIFIC_REGIONS = Collections.unmodifiableMap(specificRegionMap);
    }

    public static List<String> getRegions() {
        return REGIONS;
    }

    public static List<String> getSpecificRegions(String regionCode) {
        List<String> result = SPECIFIC_REGIONS.get(regionCode);
        if(result == null) {
            return new ArrayList<>();
        }
        return result;
    }
}
