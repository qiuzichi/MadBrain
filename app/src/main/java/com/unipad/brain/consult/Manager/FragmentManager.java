package com.unipad.brain.consult.Manager;


import com.unipad.brain.consult.dao.ConsultFragmentFactory;
import com.unipad.brain.consult.entity.ConsultTab;
import com.unipad.brain.home.MainBasicFragment;
import android.support.v4.app.Fragment;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * Created by 63 on 2016/6/20.
 */
public class FragmentManager {
    public static HashMap<String, SoftReference<Fragment>> maps;
    private static boolean needCache = true;

    public static MainBasicFragment getFragment(ConsultTab consultTab){
        if(!needCache){
            return ConsultFragmentFactory.getInstance().getFragment(consultTab);
        }
        if(maps == null){
            maps = new HashMap<>();
        }
       SoftReference<Fragment> softReference = maps.get(consultTab.getType());
        if(softReference == null){
            MainBasicFragment fragment = creatNewFragment(consultTab);
            return  fragment;
        }else{
            Fragment fragment = softReference.get();
            if(fragment == null){
                return creatNewFragment(consultTab);
            }else{
                return (MainBasicFragment)fragment;
            }
        }
    }

    private static MainBasicFragment creatNewFragment(ConsultTab consultTab) {
        Fragment fragment;
        SoftReference<Fragment> softReference;
        fragment = ConsultFragmentFactory.getInstance().getFragment(consultTab);
        softReference = new SoftReference<Fragment>(fragment);
        maps.put(consultTab.getType(), softReference);
        return  (MainBasicFragment)fragment;
    }

}
