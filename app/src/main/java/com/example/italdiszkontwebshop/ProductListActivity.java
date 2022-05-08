package com.example.italdiszkontwebshop;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProductListActivity extends AppCompatActivity {
    private static final String LOG_TAG = ProductListActivity.class.getName();
    private FirebaseUser user;

    private FrameLayout redCircle;
    private TextView countTextView;
    private int cartProducts = 0;
    private int gridNumber = 1;
    private int productLimit = 15;

    private RecyclerView mRecyclerView;
    private ArrayList<Product> mProductsData;
    private ProductAdapter mAdapter;

    private FirebaseFirestore mFirestore;
    private CollectionReference mProducts;

    private NotificationHandler mNotificationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            Log.d(LOG_TAG, "Hitelesített felhasználó");
        } else {
            Log.d(LOG_TAG, "Nem hitelesített felhasználó");
            finish();
        }


        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(
                this, gridNumber));
        mProductsData = new ArrayList<>();
        mAdapter = new ProductAdapter(this, mProductsData);
        mRecyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mProducts = mFirestore.collection("Products");

        queryData("all_products");

        mNotificationHandler = new NotificationHandler((this));
    }

    private void queryData(String category) {
        mProductsData.clear();
        Task<QuerySnapshot> query;

        if (category.equals("alcoholic_beverages")) {
            query = mProducts.orderBy("alcoholContent").whereGreaterThan("alcoholContent", 0.0).limit(productLimit).get();
        }
        else {
            query = mProducts.orderBy("price").limit(productLimit).get();
        }

        query.addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Product product = document.toObject(Product.class);
                product.setId(document.getId());
                mProductsData.add(product);
            }

            if (mProductsData.size() == 0) {
                initializeData();
                queryData("all_products");
            }

            mAdapter.notifyDataSetChanged();
        });
    }

    private void initializeData() {
        String[] productNames = getResources()
                .getStringArray(R.array.product_names);
        String[] productInfos = getResources()
                .getStringArray(R.array.product_infos);
        String[] alcoholContents = getResources()
                .getStringArray(R.array.alcohol_contents);
        int[] unitPrices = getResources()
                .getIntArray(R.array.unit_prices);
        int[] prices = getResources()
                .getIntArray(R.array.prices);
        TypedArray productImages =
                getResources().obtainTypedArray(R.array.product_images);

        for (int i = 0; i < productNames.length; i++) {
            mProducts.add(new Product(productNames[i], productInfos[i], Float.parseFloat(alcoholContents[i]),
                    unitPrices[i], prices[i], productImages.getResourceId(i, 0), 0));
        }

        productImages.recycle();
    }

    public void deleteProduct(Product product) {
        DocumentReference ref = mProducts.document(product._getId());
        ref.delete()
                .addOnSuccessListener(success -> {
                    Log.d(LOG_TAG, "Termék törölve: " + product._getId());
                })
                .addOnFailureListener(fail -> {
                    Toast.makeText(this, "Sikertelen törlés!", Toast.LENGTH_LONG).show();
                });

        queryData("all_products");
        mNotificationHandler.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.webshop_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.searchBar);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.allProducts:
                Log.d(LOG_TAG, "Összes termék opció megnyomva");
                queryData("all_products");
                return true;
            case R.id.alcoholicBeverages:
                Log.d(LOG_TAG, "Alkoholos italok opció megnyomva");
                queryData("alcoholic_beverages");
                return true;
            case R.id.cart:
                Log.d(LOG_TAG, "Kosár megnyomva");
                return true;
            case R.id.logout:
                Log.d(LOG_TAG, "Kijelentkezés megnyomva");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = (FrameLayout) rootView.findViewById(R.id.alertRedCircle);
        countTextView = (TextView) rootView.findViewById(R.id.alertText);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon(Product product) {
        cartProducts = (cartProducts + 1);
        if (0 < cartProducts) {
            countTextView.setText(String.valueOf(cartProducts));
        } else {
            countTextView.setText("");
        }

        redCircle.setVisibility((cartProducts > 0) ? VISIBLE : GONE);

        if (cartProducts == 1) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.zoom);
            redCircle.startAnimation(animation);
        }

        mProducts.document(product._getId()).update("cartedCount", product.getCartedCount() + 1)
                .addOnFailureListener(fail -> {
                    Toast.makeText(this, "Nem sikerült frissíteni a terméket.", Toast.LENGTH_LONG).show();
                });

        queryData("all_products");
        mNotificationHandler.send(product.getName());
    }
}