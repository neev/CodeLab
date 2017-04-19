package com.test.neerajal.codelab;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.AndroidPay;
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeResponseListener;
import com.braintreepayments.api.interfaces.TokenizationParametersListener;
import com.braintreepayments.api.models.AndroidPayCardNonce;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.LineItem;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.gms.wallet.fragment.SupportWalletFragment;

import java.util.Collection;

public class Braintree_Checkout extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    private static final String TOKENIZATION_KEY = "sandbox_ngby7wk5_z262mwmmnhc35t25";
    private int REQUEST_CODE = 1000;
    public static final int MASKED_WALLET_REQUEST_CODE = 888;
    private static final int REQUEST_CODE_CHANGE_MASKED_WALLET = 1002;
    private BraintreeFragment mBraintreeFragment;

    private GoogleApiClient mGoogleApiClient;
    private String MERCHANT_NAME = "My Codelab";

    private static final String TAG = MainActivity.class.getSimpleName() ;
    private SupportWalletFragment mWalletFragment;

    private MaskedWallet mMaskedWallet;
    public static final int FULL_WALLET_REQUEST_CODE = 889;
    private FullWallet mFullWallet;
    private Button confirmButton;
    private Button add_ccButton;
    String googleTransactionId;
    String merchantTranasctionId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_braintree__checkout);

        try {
            mBraintreeFragment = BraintreeFragment.newInstance(this, TOKENIZATION_KEY);
            mBraintreeFragment.getGoogleApiClient(new BraintreeResponseListener<GoogleApiClient>() {
                @Override
                public void onResponse(GoogleApiClient googleApiClient) {
                    mGoogleApiClient = googleApiClient;

                }
            });
        } catch (InvalidArgumentException iae) {
            // Something was wrong with your authorization string
        }



        AndroidPay.isReadyToPay(mBraintreeFragment, new BraintreeResponseListener<Boolean>() {
            @Override
            public void onResponse(Boolean isReadyToPay) {
                if (isReadyToPay) {
                    // show Android Pay

                    findViewById(R.id.br_layout_android_pay_checkout).setVisibility(View.VISIBLE);



                }else {
                    // Hide Android Pay buttons, show a message that Android Pay
                    // cannot be used yet, and display a traditional checkout button

                    findViewById(R.id.br_layout_android_pay_checkout)
                            .setVisibility(View.GONE);
                    Button br_add_ccButton = (Button) findViewById(R.id.br_cc_button);
                    br_add_ccButton.setVisibility(View.VISIBLE);



                }
            }
        });

    }

    public void br_requestFullWallet (View view){

        FullWalletRequest fullWalletRequest = FullWalletRequest.newBuilder()
                .setGoogleTransactionId(googleTransactionId)
                .setMerchantTransactionId(merchantTranasctionId)
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
        Wallet.Payments.loadFullWallet(mGoogleApiClient, fullWalletRequest, FULL_WALLET_REQUEST_CODE);
    }
    public void onPaymentButton(View v) {

        startAndroidPay();

    }

    public void br_creditCardActivity (View view){
        if (mMaskedWallet == null) {
            Toast.makeText(this, "Please add your Credit Card details", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Get Credit Card Details", Toast.LENGTH_SHORT).show();

    }

    public void changeOnclick(View v) {

        Wallet.Payments.changeMaskedWallet(mGoogleApiClient, googleTransactionId, merchantTranasctionId, REQUEST_CODE_CHANGE_MASKED_WALLET);

    }


    private void launchConfirmationPage() {

        findViewById(R.id.payment_button).setVisibility(View.GONE);
        findViewById(R.id.br_itemlayout).setVisibility(View.GONE);

        findViewById(R.id.br_orderconfirmation_text).setVisibility(View.VISIBLE);

        // add Total with tax fragment to the UI
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.br_confirmation_total_withtax, new CartDetailFragment())
                .commit();

        findViewById(R.id.changeWalletLayout).setVisibility(View.VISIBLE);



        findViewById(R.id.br_confirm_button).setVisibility(View.VISIBLE);

    }


    public void startAndroidPay() {

        /*BraintreeGateway gateway = new BraintreeGateway(
                Environment.SANDBOX,
                "z262mwmmnhc35t25",
                "n89ypfh4y5ntj222",
                "b5cb504eccaa2b059a1a37203a87f519"
        );*/
        AndroidPay.getTokenizationParameters(mBraintreeFragment, new TokenizationParametersListener() {
            @Override
            public void onResult(PaymentMethodTokenizationParameters parameters, Collection<Integer> allowedCardNetworks) {
                MaskedWalletRequest maskedWalletRequest = MaskedWalletRequest.newBuilder()
                        .setPaymentMethodTokenizationParameters(parameters)
                        .addAllowedCardNetworks(allowedCardNetworks)
                        .setMerchantName(MERCHANT_NAME)
                        .setCurrencyCode("USD")
                        .setEstimatedTotalPrice("15.00")
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
                        .build();
                Wallet.Payments.loadMaskedWallet(mGoogleApiClient, maskedWalletRequest, MASKED_WALLET_REQUEST_CODE);
            }
        });
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

                         googleTransactionId = mMaskedWallet.getGoogleTransactionId();
                         merchantTranasctionId = mMaskedWallet.getMerchantTransactionId();

                        launchConfirmationPage();


                        TextView email_text = (TextView) findViewById(R.id.CCemail);
                        TextView ccdetails_text = (TextView) findViewById(R.id.CCdetails);

                        email_text.setText(mMaskedWallet.getBuyerBillingAddress().getName());


                        String card_type = mMaskedWallet.getInstrumentInfos()[0].getInstrumentType();
                        String card_num = mMaskedWallet.getInstrumentInfos()[0].getInstrumentDetails();

                       String cardholder_name =  mMaskedWallet.getBuyerBillingAddress().getName();
                        ccdetails_text.setText(card_type + " " + card_num);


                        Toast.makeText(this, "Braintree - Got Masked Wallet  " , Toast.LENGTH_SHORT).show();
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


                }
                // you may also want to use the new masked wallet data here, say to recalculate
                // shipping or taxes if shipping address changed
                break;

            case FULL_WALLET_REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        mFullWallet = data
                                .getParcelableExtra(WalletConstants.EXTRA_FULL_WALLET);

                        AndroidPay.tokenize(mBraintreeFragment, mFullWallet);

                        // Show the credit card number
                        Toast.makeText(this,
                                "Got Full Wallet - Payment Nonce : ",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case WalletConstants.RESULT_ERROR:
                        Toast.makeText(this, "An Error Occurred", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }





    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        if (paymentMethodNonce instanceof AndroidPayCardNonce) {
            AndroidPayCardNonce card = (AndroidPayCardNonce) paymentMethodNonce;
        }
    }




    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
