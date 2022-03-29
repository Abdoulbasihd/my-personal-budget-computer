package cm.abimmobiledev.mybudgetizer.ui.earning;

import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;
import cm.abimmobiledev.mybudgetizer.nav.IncNavigator;

/**
 * create an instance of this fragment.
 */
public class BottomSheetMoreEarningsFragment extends BottomSheetDialogFragment {


    public BottomSheetMoreEarningsFragment() {
        // Required empty public constructor
    }

    // Rename and change types and number of parameters
    public static BottomSheetMoreEarningsFragment newInstance() {
        BottomSheetMoreEarningsFragment fragment = new BottomSheetMoreEarningsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_more_earnings, container, false);

        MaterialButton todayIncome = view.findViewById(R.id.view_today_incomes);
        MaterialButton thisMonthIncome = view.findViewById(R.id.view_this_month_incomes);
        MaterialButton thisYearIncome = view.findViewById(R.id.view_this_year_incomes);
        MaterialButton aDayIncome = view.findViewById(R.id.filter_for_a_given_date);

        todayIncome.setOnClickListener(todayView -> IncNavigator.openEarningsSearch(getActivity(), ExNavigation.EXPENSE_OF_TODAY));

        thisMonthIncome.setOnClickListener(monthExpView -> IncNavigator.openEarningsSearch(getActivity(), ExNavigation.EXPENSE_OF_THIS_MONTH));

        thisYearIncome.setOnClickListener(yearView -> IncNavigator.openEarningsSearch(getActivity(), ExNavigation.EXPENSE_OF_THIS_YEAR));

        aDayIncome.setOnClickListener(yearView -> IncNavigator.openEarningsSearch(getActivity(), ExNavigation.EXPENSE_OF_A_DATE));

        return view;
    }
}