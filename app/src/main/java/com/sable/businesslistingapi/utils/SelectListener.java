package com.sable.businesslistingapi.utils;

/**
 * Sable Business Directory on 09/01/2021
 * Contact Email : admin@sablebusinessdirectory.com
 */


public interface SelectListener {
    void Select(int position, CharSequence text);
    void Select(int position, CharSequence text, String id);
    /*void Select(View view, int position, CharSequence text, String id, float additional_price);*/
}
