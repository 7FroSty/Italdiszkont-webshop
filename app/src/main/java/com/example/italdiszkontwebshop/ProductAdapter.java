package com.example.italdiszkontwebshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements Filterable {
    private ArrayList<Product> mProductData;
    private ArrayList<Product> mProductDataAll;
    private Context mContext;
    private int lastPosition = -1;

    ProductAdapter(Context context, ArrayList<Product> productsData) {
        this.mProductData = productsData;
        this.mProductDataAll = productsData;
        this.mContext = context;
    }

    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.product, parent, false));
    }

    @Override
    public void onBindViewHolder(ProductAdapter.ViewHolder holder, int position) {
        Product currentProduct = mProductData.get(position);

        holder.bindTo(currentProduct);

        if(holder.getBindingAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getBindingAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mProductData.size();
    }

    @Override
    public Filter getFilter() {
        return shoppingFilter;
    }

    private Filter shoppingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Product> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0) {
                results.count = mProductDataAll.size();
                results.values = mProductDataAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(Product product : mProductDataAll) {
                    if(product.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(product);
                    }
                }

                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mProductData = (ArrayList)filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mNameTV;
        private TextView mInfoTV;
        private TextView mAlcoholContentTV;
        private TextView mUnitPriceTV;
        private TextView mPriceTV;
        private ImageView mImageIV;

        ViewHolder(View productView) {
            super(productView);

            mNameTV = itemView.findViewById(R.id.productNameTV);
            mInfoTV = itemView.findViewById(R.id.productInfoTV);
            mAlcoholContentTV = itemView.findViewById(R.id.alcoholContentTV);
            mUnitPriceTV = itemView.findViewById(R.id.unitPriceTV);
            mPriceTV = itemView.findViewById(R.id.priceTV);
            mImageIV = itemView.findViewById(R.id.productImageIV);
        }

        void bindTo(Product currentProduct){
            mNameTV.setText(currentProduct.getName());
            mInfoTV.setText(currentProduct.getInfo());
            mAlcoholContentTV.setText("Alkoholtartalom: " + currentProduct.getAlcoholContent() + "%");
            mUnitPriceTV.setText("Egységár: " + currentProduct.getUnitPrice() + " Ft/l");
            mPriceTV.setText(currentProduct.getPrice() + " Ft");

            Glide.with(mContext).load(currentProduct.getImage()).into(mImageIV);

            itemView.findViewById(R.id.addToCartB).setOnClickListener(view ->
                            ((ProductListActivity)mContext).updateAlertIcon(currentProduct));

            itemView.findViewById(R.id.deleteFromCartB).setOnClickListener(view ->
                    ((ProductListActivity)mContext).deleteProduct(currentProduct));
        }
    }
}
