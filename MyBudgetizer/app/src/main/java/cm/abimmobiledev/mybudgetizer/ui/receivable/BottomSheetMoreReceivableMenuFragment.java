package cm.abimmobiledev.mybudgetizer.ui.receivable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.nav.DebtNavigator;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;
import cm.abimmobiledev.mybudgetizer.nav.ReceivNav;

public class BottomSheetMoreReceivableMenuFragment extends BottomSheetDialogFragment {

    private String accountName;
    private String currency;

    public BottomSheetMoreReceivableMenuFragment() {
        // Required empty public constructor
    }

    public static BottomSheetMoreReceivableMenuFragment newInstance(String accountName, String currency) {
        BottomSheetMoreReceivableMenuFragment fragment = new BottomSheetMoreReceivableMenuFragment();
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
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_more_receivable_menu, container, false);

        MaterialButton assignedToday = view.findViewById(R.id.assigned_today);
        assignedToday.setOnClickListener(v -> ReceivNav.openReceivablesSearch(getActivity(), DebtNavigator.DEBT_CONTRACTED_TODAY, accountName, currency));

        MaterialButton expiresToday = view.findViewById(R.id.expires_today);
        expiresToday.setOnClickListener(v -> ReceivNav.openReceivablesSearch(getActivity(), DebtNavigator.DEBT_EXPIRES_TODAY, accountName, currency));

        MaterialButton paidToday = view.findViewById(R.id.paid_today);
        paidToday.setOnClickListener(v -> ReceivNav.openReceivablesSearch(getActivity(), DebtNavigator.DEBT_PAID_TODAY, accountName, currency));

        MaterialButton assignedThisMonth = view.findViewById(R.id.assigned_this_month);
        assignedThisMonth.setOnClickListener(v -> ReceivNav.openReceivablesSearch(getActivity(), DebtNavigator.DEBT_CONTRACTED_THIS_MONTH, accountName, currency));

        MaterialButton expiresThisMonth = view.findViewById(R.id.expires_this_month);
        expiresThisMonth.setOnClickListener(v -> ReceivNav.openReceivablesSearch(getActivity(), DebtNavigator.DEBT_EXPIRES_THIS_MONTH, accountName, currency));

        MaterialButton paidThisMonth = view.findViewById(R.id.paid_this_month);
        paidThisMonth.setOnClickListener(v -> ReceivNav.openReceivablesSearch(getActivity(), DebtNavigator.DEBT_PAID_THIS_MONTH, accountName, currency));

        MaterialButton assignedThisYear = view.findViewById(R.id.assigned_this_year);
        assignedThisYear.setOnClickListener(v -> ReceivNav.openReceivablesSearch(getActivity(), DebtNavigator.DEBT_CONTRACTED_THIS_YEAR, accountName, currency));

        MaterialButton expiresThisYear = view.findViewById(R.id.expires_this_year);
        expiresThisYear.setOnClickListener(v -> ReceivNav.openReceivablesSearch(getActivity(), DebtNavigator.DEBT_EXPIRES_THIS_YEAR, accountName, currency));

        MaterialButton paidThisYear = view.findViewById(R.id.paid_this_year);
        paidThisYear.setOnClickListener(v -> ReceivNav.openReceivablesSearch(getActivity(), DebtNavigator.DEBT_PAID_THIS_YEAR, accountName, currency));

        MaterialButton otherFilter = view.findViewById(R.id.filter_otherwise);
        otherFilter.setOnClickListener(v -> {
            //here : you could combine two search param to search... search on date and on param paid/unpaid
            //date to consider could be : contraction date, due date and payment date
            //when payment date is selected, this mean that payment status is always true
            //it won't be too easy, but you can do this
            ReceivNav.openReceivablesSearch(getActivity(), DebtNavigator.DEBT_OTHER_SEARCH, accountName, currency);
        });


        return view;
    }
}