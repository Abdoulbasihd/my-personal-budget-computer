package cm.abimmobiledev.mybudgetizer.ui.debt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.nav.DebtNavigator;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomSheetMoreDebtsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomSheetMoreDebtsFragment extends BottomSheetDialogFragment {

    private String accountName;
    private String currency;

    public BottomSheetMoreDebtsFragment() {
        // Required empty public constructor
    }

    public static BottomSheetMoreDebtsFragment newInstance(String accountName, String currency) {
        BottomSheetMoreDebtsFragment fragment = new BottomSheetMoreDebtsFragment();
        Bundle args = new Bundle();
        args.putString(ExNavigation.CURRENCY_PARAM, currency);
        args.putString(ExNavigation.ACC_NAME_PARAM, accountName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments()!=null) {
            accountName = getArguments().getString(ExNavigation.ACC_NAME_PARAM);
            currency = getArguments().getString(ExNavigation.CURRENCY_PARAM);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_more_debts, container, false);

        //TODO define search params...
        MaterialButton incurredToday = view.findViewById(R.id.incurred_today);
        incurredToday.setOnClickListener(v -> DebtNavigator.openDebtsSearch(getActivity(), DebtNavigator.DEBT_CONTRACTED_TODAY, accountName, currency));

        MaterialButton expiresToday = view.findViewById(R.id.expires_today);
        expiresToday.setOnClickListener(v -> DebtNavigator.openDebtsSearch(getActivity(), DebtNavigator.DEBT_EXPIRES_TODAY, accountName, currency));

        MaterialButton paidToday = view.findViewById(R.id.paid_today);
        paidToday.setOnClickListener(v -> DebtNavigator.openDebtsSearch(getActivity(), DebtNavigator.DEBT_PAID_TODAY, accountName, currency));

        MaterialButton incurredThisMonth = view.findViewById(R.id.incurred_this_month);
        incurredThisMonth.setOnClickListener(v -> DebtNavigator.openDebtsSearch(getActivity(), DebtNavigator.DEBT_CONTRACTED_THIS_MONTH, accountName, currency));

        MaterialButton expiresThisMonth = view.findViewById(R.id.expires_this_month);
        expiresThisMonth.setOnClickListener(v -> DebtNavigator.openDebtsSearch(getActivity(), DebtNavigator.DEBT_EXPIRES_THIS_MONTH, accountName, currency));

        MaterialButton paidThisMonth = view.findViewById(R.id.paid_this_month);
        paidThisMonth.setOnClickListener(v -> DebtNavigator.openDebtsSearch(getActivity(), DebtNavigator.DEBT_PAID_THIS_MONTH, accountName, currency));

        MaterialButton incurredThisYear = view.findViewById(R.id.incurred_this_year);
        incurredThisYear.setOnClickListener(v -> DebtNavigator.openDebtsSearch(getActivity(), DebtNavigator.DEBT_CONTRACTED_THIS_YEAR, accountName, currency));

        MaterialButton expiresThisYear = view.findViewById(R.id.expires_this_year);
        expiresThisYear.setOnClickListener(v -> DebtNavigator.openDebtsSearch(getActivity(), DebtNavigator.DEBT_EXPIRES_THIS_YEAR, accountName,currency));

        MaterialButton paidThisYear = view.findViewById(R.id.paid_this_year);
        paidThisYear.setOnClickListener(v -> DebtNavigator.openDebtsSearch(getActivity(), DebtNavigator.DEBT_PAID_THIS_YEAR, accountName, currency));

        MaterialButton otherFilter = view.findViewById(R.id.filter_otherwise);
        otherFilter.setOnClickListener(v -> {
            //here : you could combine two search param to search... search on date and on param paid/unpaid
            //date to consider could be : contraction date, due date and payment date
            //when payment date is selected, this mean that payment status is always true
            //it won't be too easy, but you can do this
            DebtNavigator.openDebtsSearch(getActivity(), DebtNavigator.DEBT_OTHER_SEARCH, accountName, currency);
        });


        return view;
    }
}