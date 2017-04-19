/*
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.test.neerajal.codelab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple re-usable fragment that displays the current cart details.
 * The fragment needs an item id and displays the item name, item image, price, tax and shipping
 * costs associated with the item.
 */
public class CartDetailFragment extends Fragment {

    private int mItemId;



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart_detail, container, false);



        TextView itemName = (TextView) view.findViewById(R.id.text_item_name);
        itemName.setText("Google I/O Sticker");



        TextView itemPrice = (TextView) view.findViewById(R.id.text_item_price);
        itemPrice.setText("10.00");
        TextView shippingCost = (TextView) view.findViewById(R.id.text_shipping_price);
        TextView tax = (TextView) view.findViewById(R.id.text_tax_price);
        TextView total = (TextView) view.findViewById(R.id.text_total_price);

        shippingCost.setText("0.00");
        tax.setText(".10");
        total.setText("10.10");

        return view;
    }



}
