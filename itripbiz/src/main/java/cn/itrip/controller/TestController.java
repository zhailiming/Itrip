package cn.itrip.controller;
import cn.itrip.beans.pojo.ItripAreaDic;
import cn.itrip.beans.vo.TempVO;
import cn.itrip.common.ChineseToEnglish;
import cn.itrip.common.EmptyUtils;
import cn.itrip.service.areadic.ItripAreaDicService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 用于临时测试 以及完成一些非必要性功能的controller
 * Created by XX on 17-4-25.
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @Resource
    private ItripAreaDicService itripAreaDicService;

    /***
     * 将区域数据进行组合按照拼音分组输出
     * @return
     * @throws Exception
     */
    @RequestMapping("/queryArea")
    @ResponseBody
    public List<TempVO> queryArea() throws Exception {
        List<TempVO> areaDicTempVoList = new ArrayList<TempVO>();
        List<ItripAreaDic> itripAreaDicList = null;
        Map<String, Object> param = new HashMap<>();
        param.put("level", 2);
        param.put("isChina",2);
        itripAreaDicList = itripAreaDicService.getItripAreaDicListByMap(param);
        Character [] array = new Character[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        for (int i = 0; i < array.length; i++) {
            TempVO vo=new TempVO();
            List<Map<String, Object>> list = new ArrayList<>();
            for (ItripAreaDic itripAreaDic : itripAreaDicList) {
                Character str1=array[i];
                Character str2=new Character(itripAreaDic.getPinyin().charAt(0));
                if (Character.toUpperCase(str1)==Character.toUpperCase(str2)) {
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("cityId", itripAreaDic.getId());
                    map1.put("cityName", itripAreaDic.getName());
                    map1.put("pinyin", itripAreaDic.getPinyin().toUpperCase());
                    list.add(map1);
                }
            }
            if (EmptyUtils.isNotEmpty(list)) {
                vo.setRootName(new Character(array[i]).toString());
                vo.setLeafs(list);
                areaDicTempVoList.add(vo);
            }
        }
        return areaDicTempVoList;
    }

    /***
     * 为区域表加上拼音
     * @throws Exception
     */
    @RequestMapping("/execute")
    public void execute() throws Exception {
        List<ItripAreaDic> itripAreaDicList = null;
        Map<String, Object> param = new HashMap<>();
        param.put("level", 2);
        itripAreaDicList = itripAreaDicService.getItripAreaDicListByMap(param);
        for (ItripAreaDic dic:itripAreaDicList){
            dic.setPinyin(ChineseToEnglish.getPinYin(dic.getName()));
            itripAreaDicService.itriptxModifyItripAreaDic(dic);
        }
    }
}

