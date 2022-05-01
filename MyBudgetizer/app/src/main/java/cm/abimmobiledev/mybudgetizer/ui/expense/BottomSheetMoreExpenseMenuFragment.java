package cm.abimmobiledev.mybudgetizer.ui.expense;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;


public class BottomSheetMoreExpenseMenuFragment extends BottomSheetDialogFragment {

    private String accountName;
    private String currency;

    public BottomSheetMoreExpenseMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            accountName = getArguments().getString(ExNavigation.ACC_NAME_PARAM);
            currency = getArguments().getString(ExNavigation.CURRENCY_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_more_expense_menu, container, false);

        MaterialButton today = view.findViewById(R.id.view_today_expenses);
        MaterialButton thisMonth = view.findViewById(R.id.view_this_month_expenses);
        MaterialButton thisYear = view.findViewById(R.id.view_this_year_expenses);
        MaterialButton searchOther = view.findViewById(R.id.filter_for_a_given_date);

        today.setOnClickListener(todayView -> ExNavigation.openExpenseSearch(getActivity(), ExNavigation.EXPENSE_OF_TODAY, accountName, currency));

        thisMonth.setOnClickListener(monthExpView -> ExNavigation.openExpenseSearch(getActivity(), ExNavigation.EXPENSE_OF_THIS_MONTH, accountName, currency));

        thisYear.setOnClickListener(yearView -> ExNavigation.openExpenseSearch(getActivity(), ExNavigation.EXPENSE_OF_THIS_YEAR, accountName, currency));

        searchOther.setOnClickListener(yearView -> ExNavigation.openExpenseSearch(getActivity(), ExNavigation.EXPENSE_OF_A_DATE, accountName, currency));

        return view;
    }
}