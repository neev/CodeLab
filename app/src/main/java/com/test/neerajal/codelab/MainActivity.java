package com.test.neerajal.codelab;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.LineItem;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.PaymentMethodTokenizationType;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.gms.wallet.fragment.SupportWalletFragment;
import com.google.android.gms.wallet.fragment.WalletFragmentInitParams;
import com.google.android.gms.wallet.fragment.WalletFragmentMode;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;
import com.google.android.gms.wallet.fragment.WalletFragmentStyle;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = MainActivity.class.getSimpleName() ;
    private SupportWalletFragment mWalletFragment;
        public static final int MASKED_WALLET_REQUEST_CODE = 888;
    private static final int REQUEST_CODE_CHANGE_MASKED_WALLET = 1002;
        public static final String WALLET_FRAGMENT_ID = "wallet_fragment";
        private MaskedWallet mMaskedWallet;
        private GoogleApiClient mGoogleApiClient;
        public static final int FULL_WALLET_REQUEST_CODE = 889;
        private FullWallet mFullWallet;
        private Button confirmButton;
        private Button add_ccButton;

    // You will need to use your live API key even while testing
    public static final String PUBLISHABLE_KEY = "pk_test_w639eDA8MMLo1R3rXmX2DMmV";

    // Unique identifiers for asynchronous requests:
    private static final int LOAD_MASKED_WALLET_REQUEST_CODE = 1000;
    private static final int LOAD_FULL_WALLET_REQUEST_CODE = 1001;





    @Override
        protected void onCreate (Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(this, 0, this)
                .addApi(Wallet.API, new Wallet.WalletOptions.Builder()
                        .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                        .setTheme(WalletConstants.THEME_LIGHT)
                        .build())
                .build();

        confirmButton = (Button) findViewById(R.id.confirm_button);
         add_ccButton = (Button) findViewById(R.id.cc_button);
        /*-----isReadyToPay()-----*/




        Wallet.Payments.isReadyToPay(mGoogleApiClient).setResultCallback(
                new ResultCallback<BooleanResult>() {
                    @Override
                    public void onResult(@NonNull BooleanResult booleanResult) {

                        if (booleanResult.getStatus().isSuccess()) {
                            if (booleanResult.getValue()) {
                                // Show Android Pay buttons and hide regular checkout button

                                add_ccButton.setVisibility(View.GONE);
                                // Check if WalletFragment exists
                                mWalletFragment = (SupportWalletFragment) getSupportFragmentManager()
                                        .findFragmentByTag(WALLET_FRAGMENT_ID);

                                if (mWalletFragment == null) {


                                    // Wallet fragment style
                                    WalletFragmentStyle walletFragmentStyle = new WalletFragmentStyle()
                                            .setBuyButtonText(WalletFragmentStyle.BuyButtonText.BUY_WITH)
                                            .setBuyButtonWidth(WalletFragmentStyle.Dimension.MATCH_PARENT);

                                    // Wallet fragment options
                                    WalletFragmentOptions walletFragmentOptions = WalletFragmentOptions.newBuilder()
                                            .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                                            .setFragmentStyle(walletFragmentStyle)
                                            .setTheme(WalletConstants.THEME_LIGHT)
                                            .setMode(WalletFragmentMode.BUY_BUTTON)
                                            .build();

                                    // Initialize the WalletFragment
                                    WalletFragmentInitParams.Builder startParamsBuilder =
                                            WalletFragmentInitParams.newBuilder()
                                                    .setMaskedWalletRequest(generateMaskedWalletRequest())
                                                    .setMaskedWalletRequestCode(MASKED_WALLET_REQUEST_CODE)
                                                    .setAccountName("My Codelab");
                                    mWalletFragment = SupportWalletFragment.newInstance(walletFragmentOptions);
                                    mWalletFragment.initialize(startParamsBuilder.build());

                                    // Add the WalletFragment to the UI
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.wallet_button_holder, mWalletFragment, WALLET_FRAGMENT_ID)
                                            .commit();
                                }


                            } else {
                                // Hide Android Pay buttons, show a message that Android Pay
                                // cannot be used yet, and display a traditional checkout button

                                findViewById(R.id.layout_android_pay_checkout)
                                        .setVisibility(View.GONE);

                                add_ccButton.setVisibility(View.VISIBLE);

                                String androidPayApp = "com.google.android.apps.walletnfcrel";
                                Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(androidPayApp);


                                // check whether the user's device is Android Pay compatible
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                                        && getBaseContext().getPackageManager().hasSystemFeature(
                                        PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)){
                                    if (intent != null) {
                                        getBaseContext().startActivity(intent);  // launch Android Pay app
                                    } else {
// Android Pay app not installed
                                    }
                                }




                            }
                        } else {
                            // Error making isReadyToPay call
                            Log.e(TAG, "isReadyToPay:" + booleanResult.getStatus());
                        }
                    }
                });








    }

    public void requestFullWallet (View view){
        if (mMaskedWallet == null) {
            Toast.makeText(this, "No masked wallet, can't confirm", Toast.LENGTH_SHORT).show();
            return;
        }
        Wallet.Payments.loadFullWallet(mGoogleApiClient,
                generateFullWalletRequest(mMaskedWallet.getGoogleTransactionId()),
                LOAD_FULL_WALLET_REQUEST_CODE);
    }


    public void creditCardActivity (View view){
        if (mMaskedWallet == null) {
            Toast.makeText(this, "Please add your Credit Card details", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Get Credit Card Details", Toast.LENGTH_SHORT).show();

    }




    private MaskedWalletRequest generateMaskedWalletRequest() {
        // This is just an test publicKey for the purpose of this codelab.
        // To learn how to generate your own visit:
        // https://github.com/android-pay/androidpay-quickstart

        /*

        String publicKey = "BO39Rh43UGXMQy5PAWWe7UGWd2a9YRjNLPEEVe+zWIbdIgALcDcnYCuHbmrrzl7h8FZjl6RCzoi5/cDrqXNRVSo=";
        PaymentMethodTokenizationParameters parameters =
                PaymentMethodTokenizationParameters.newBuilder()
                        .setPaymentMethodTokenizationType(
                                PaymentMethodTokenizationType.NETWORK_TOKEN)
                        .addParameter("publicKey", publicKey)
                        .build();
            */


        PaymentMethodTokenizationParameters parameters =
                PaymentMethodTokenizationParameters.newBuilder()
                        // Request credit card tokenization with Stripe by specifying tokenization parameters:

                                .setPaymentMethodTokenizationType(PaymentMethodTokenizationType.PAYMENT_GATEWAY)
                                .addParameter("gateway", "stripe")
                                .addParameter("stripe:publishableKey", PUBLISHABLE_KEY)
                                //.addParameter("stripe:version", com.stripe.android.)
                                .build();

        MaskedWalletRequest maskedWalletRequest =
                MaskedWalletRequest.newBuilder()
                        .setMerchantName("My Codelab")
                        .setPhoneNumberRequired(true)
                        .setShippingAddressRequired(true)
                        .setAllowPrepaidCard(false)
                        .setCurrencyCode("USD")
                        .setCart(Cart.newBuilder()
                                .setCurrencyCode("USD")
                                .setTotalPrice("10.00")
                                .addLineItem(LineItem.newBuilder()
                                        .setCurrencyCode("USD")
                                        .setDescription("Google I/O Sticker")
                                        .setQuantity("1")
                                        .setUnitPrice("10.00")
                                        .setTotalPrice("10.00")
                                        .build())
                                .build())
                        .setEstimatedTotalPrice("15.00")
                        .setPaymentMethodTokenizationParameters(parameters)
                        .build();
        return maskedWalletRequest;

    }


    private FullWalletRequest generateFullWalletRequest(String googleTransactionId) {
        FullWalletRequest fullWalletRequest = FullWalletRequest.newBuilder()
                .setGoogleTransactionId(googleTransactionId)
                .setCart(Cart.newBuilder()
                        .setCurrencyCode("USD")
                        .setTotalPrice("10.10")
                        .addLineItem(LineItem.newBuilder()
                                .setCurrencyCode("USD")
                                .setDescription("Google I/O Sticker")
                                .setQuantity("1")
                                .setUnitPrice("10.00")
                                .setTotalPrice("10.00")
                                .build())
                        .addLineItem(LineItem.newBuilder()
                                .setCurrencyCode("USD")
                                .setDescription("Tax")
                                .setRole(LineItem.Role.TAX)
                                .setTotalPrice(".10")
                                .build())
                        .build())
                .build();
        return fullWalletRequest;
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // GoogleApiClient failed to connect, we should log the error and retry
    }


    private void launchConfirmationPage() {

        findViewById(R.id.wallet_button_holder).setVisibility(View.GONE);
        findViewById(R.id.itemlayout).setVisibility(View.GONE);

        findViewById(R.id.orderconfirmation_text).setVisibility(View.VISIBLE);

        // add Total with tax fragment to the UI
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.confirmation_total_withtax, new CartDetailFragment())
                .commit();

        //change wallet fragment




// [START wallet_fragment_options]
        WalletFragmentOptions walletFragmentOptions = WalletFragmentOptions.newBuilder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST)

                .setTheme(WalletConstants.THEME_DARK)
                .setMode(WalletFragmentMode.SELECTION_DETAILS)
                .build();
        mWalletFragment = SupportWalletFragment.newInstance(walletFragmentOptions);
        // [END wallet_fragment_options]

        // Now initialize the Wallet Fragment
        String accountName = "My CodeLab";
        WalletFragmentInitParams.Builder startParamsBuilder = WalletFragmentInitParams.newBuilder()
                .setMaskedWallet(mMaskedWallet)
                .setMaskedWalletRequestCode(REQUEST_CODE_CHANGE_MASKED_WALLET)
                .setAccountName(accountName);
        mWalletFragment.initialize(startParamsBuilder.build());

        // add Wallet fragment to the UI
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.dynamic_wallet_masked_wallet_fragment, mWalletFragment)
                .commit();




        confirmButton.setVisibility(View.VISIBLE);

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MASKED_WALLET_REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        mMaskedWallet = data
                                .getParcelableExtra(WalletConstants.EXTRA_MASKED_WALLET);

                        ProgressDialog mProgressDialog = new ProgressDialog(this);
                        mProgressDialog.setIndeterminate(true);
                        mProgressDialog.setMessage("Loading...");

                        //change wallet fragment

                        launchConfirmationPage();


                        Toast.makeText(this, "Got Masked Wallet", Toast.LENGTH_SHORT).show();
                        break;
                    case RESULT_CANCELED:
                        // The user canceled the operation
                        break;
                    case WalletConstants.RESULT_ERROR:
                        Toast.makeText(this, "An Error Occurred", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;

            case REQUEST_CODE_CHANGE_MASKED_WALLET:
                if (resultCode == RESULT_OK &&
                        data.hasExtra(WalletConstants.EXTRA_MASKED_WALLET)) {
                    mMaskedWallet = data.getParcelableExtra(WalletConstants.EXTRA_MASKED_WALLET);

                    /*((FullWalletConfirmationButtonFragment) getResultTargetFragment())
                            .updateMaskedWallet(mMaskedWallet);*/
                }
                // you may also want to use the new masked wallet data here, say to recalculate
                // shipping or taxes if shipping address changed
                break;

            /*case LOAD_MASKED_WALLET_REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        mMaskedWallet = data
                                .getParcelableExtra(WalletConstants.EXTRA_MASKED_WALLET);

                       *//* ///from stripe Docs

                        FullWalletRequest fullWalletRequest = FullWalletRequest.newBuilder()
                                .setCart(Cart.newBuilder()
                                        .setCurrencyCode("USD")
                                        .setTotalPrice("20.00")
                                        .addLineItem(LineItem.newBuilder() // Identify item being purchased
                                                .setCurrencyCode("USD")
                                                .setQuantity("1")
                                                .setDescription("Premium Llama Food")
                                                .setTotalPrice("20.00")
                                                .setUnitPrice("20.00")
                                                .build())
                                        .build())
                                .setGoogleTransactionId(mMaskedWallet.getGoogleTransactionId())
                                .build();
                        Wallet.Payments.loadFullWallet(mGoogleApiClient, fullWalletRequest, LOAD_FULL_WALLET_REQUEST_CODE);
*//*
                        //////////
                        Toast.makeText(this, "Got Load Masked Wallet", Toast.LENGTH_SHORT).show();
                        break;
                    case RESULT_CANCELED:
                        // The user canceled the operation
                        break;
                    case WalletConstants.RESULT_ERROR:
                        Toast.makeText(this, "An Error Occurred", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
*/

            /*case FULL_WALLET_REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        mFullWallet = data
                                .getParcelableExtra(WalletConstants.EXTRA_FULL_WALLET);

                        //////from stripe docs

                        String tokenJSON = mFullWallet.getPaymentMethodToken().getToken();


                        // Show the credit card number
                        Toast.makeText(this,
                                "Got Full Wallet, Done!"+" Stripe : "+ tokenJSON,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case WalletConstants.RESULT_ERROR:
                        Toast.makeText(this, "An Error Occurred", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;*/

            case LOAD_FULL_WALLET_REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        mFullWallet = data
                                .getParcelableExtra(WalletConstants.EXTRA_FULL_WALLET);

                        //////from stripe docs

                        String tokenJSON = mFullWallet.getPaymentMethodToken().getToken();



                        // Show the credit card number
                        Toast.makeText(this,
                                "Got Full Wallet, Done!  " +" Stripe : "+ tokenJSON,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case WalletConstants.RESULT_ERROR:
                        Toast.makeText(this, "An Error Occurred", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }


    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}



