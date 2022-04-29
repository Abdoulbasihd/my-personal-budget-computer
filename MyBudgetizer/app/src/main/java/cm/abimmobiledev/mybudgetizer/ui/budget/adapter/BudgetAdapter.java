package cm.abimmobiledev.mybudgetizer.ui.budget.adapter;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.database.BudgetizerAppDatabase;
import cm.abimmobiledev.mybudgetizer.database.entity.Budget;
import cm.abimmobiledev.mybudgetizer.database.entity.BudgetWithExpenses;
import cm.abimmobiledev.mybudgetizer.exception.BudgetizerGeneralException;
import cm.abimmobiledev.mybudgetizer.useful.Util;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    private final List<Budget> budgets;

    public BudgetAdapter(List<Budget> budgets) {
        this.budgets = budgets;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View budgetView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_budget, parent, false);
        return new BudgetAdapter.BudgetViewHolder(budgetView);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        Budget budget = budgets.get(position);


        if (budget.getSticker().isEmpty())
            holder.budgetStickerCard.setVisibility(View.GONE);
        else
            holder.budgetStickerCard.setVisibility(View.VISIBLE);

        holder.budgetSticker.setText(budget.getSticker());
        holder.budgetDueDate.setText(budget.getEndDate());
        holder.budgetDebutDate.setText(budget.getBeginningDate());
        holder.budgetAmount.setText(String.valueOf(budget.getAmount()));
        holder.budgetTitle.setText(budget.getEntitled());

        if (budget.getEntitled()!=null && budget.getEntitled().length()>0){
            holder.budgetLogoView.setText(String.valueOf(budget.getEntitled().toUpperCase(Locale.ROOT).charAt(0)));
        }

        //count used by selecting expenses of this budget...
        ProgressDialog selectComputeConsumedProgress = Util.initProgressDialog(holder.budgetTitle.getContext(), "En cours");
        selectComputeConsumedProgress.show();

        ExecutorService debtUpdateExecService = Executors.newSingleThreadExecutor();
        debtUpdateExecService.execute(() -> {
            BudgetizerAppDatabase appDatabaseBudgetConsumedSelector = BudgetizerAppDatabase.getInstance(holder.budgetTitle.getContext());
            BudgetWithExpenses budgetWithExpenses = appDatabaseBudgetConsumedSelector.budgetDAO().getExpensesOfGivenBudget(budget.getBudgetId());

            try {
                double consumedAmount = getConsumedAmount(budgetWithExpenses);
                int consumedPercentage = (int) getConsumedPercent(consumedAmount, budget);

                new Handler(Looper.getMainLooper()).post(() -> {
                    selectComputeConsumedProgress.dismiss();
                    holder.budgetProgression.setProgress(consumedPercentage);
                    holder.consumedPercentageValue.setText(consumedPercentage+" %");

                });

            } catch (BudgetizerGeneralException ignore) { }

        });


        holder.deleteContent.setOnClickListener(deleteItemView -> {

            AlertDialog.Builder myBudgetAlertDeletionDialog = Util.initAlertDialogBuilder(holder.deleteContent.getContext(), holder.deleteContent.getContext().getString(R.string.state),  holder.deleteContent.getContext().getString(R.string.do_you_really_want_to_delete_this_budget_it_ll_lead_to_delete_all_linked_expenses));
            myBudgetAlertDeletionDialog.setNegativeButton(holder.deleteContent.getContext().getString(R.string.cancel), (dialog, which) -> {
                //Ne rien faire ici
            });
            myBudgetAlertDeletionDialog.setPositiveButton(holder.deleteContent.getContext().getString(R.string.go_on), (dialog, which) -> {
                ProgressDialog budgetDelProgress = Util.initProgressDialog(holder.deleteContent.getContext(), holder.deleteContent.getContext().getString(R.string.deleting));
                budgetDelProgress.show();

                AlertDialog.Builder myBudgetDeletorDialog = Util.initAlertDialogBuilder(holder.deleteContent.getContext(), holder.deleteContent.getContext().getString(R.string.state),  holder.deleteContent.getContext().getString(R.string.budget_deleted_with_associated_expended_actualize_please));


                ExecutorService budgetDelService = Executors.newSingleThreadExecutor();
                budgetDelService.execute(() -> {
                    BudgetizerAppDatabase budgetDelAppDatabase = BudgetizerAppDatabase.getInstance(holder.deleteContent.getContext());
                    budgetDelAppDatabase.budgetDAO().delete(budget);
                    budgets.remove(budget);

                    new Handler(Looper.getMainLooper()).post(() -> {
                        budgetDelProgress.dismiss();
                        myBudgetDeletorDialog.setPositiveButton("Ok", (dialog1, which1) -> {
                            //nothing to do here, just close
                        }).show();
                    });



                    notifyItemRemoved(position);
                    notifyItemChanged(position);

                });
            });
            myBudgetAlertDeletionDialog.show();


        });


    }

    @Override
    public int getItemCount() {
        return budgets.size();
    }

    public static double getConsumedAmount(BudgetWithExpenses budgetWithExpenses) throws BudgetizerGeneralException {
        if (budgetWithExpenses==null || budgetWithExpenses.expenses == null)
            throw new BudgetizerGeneralException("param must be initialized");

        double amountConsumed = 0;
        for(int counter=0; counter<budgetWithExpenses.expenses.size(); counter++){
            amountConsumed = amountConsumed + budgetWithExpenses.expenses.get(counter).getAmount();
        }

        return amountConsumed;
    }

    public static double getConsumedPercent(double consumed, Budget budget) throws BudgetizerGeneralException {
        if (budget==null)
            throw new BudgetizerGeneralException("param must be init");

        if (budget.getAmount()==consumed)
            return 100;

        if (budget.getAmount()==0)
            throw new BudgetizerGeneralException("budget's amount must be different of zero when having expenses...");

        return (consumed * 100) / budget.getAmount(); // percentage = (consumed * 100) / total to consume
    }

    public static class BudgetViewHolder extends RecyclerView.ViewHolder {

        final TextView budgetTitle;
        final TextView budgetLogoView;
        final TextView budgetAmount;
        final TextView budgetDebutDate;
        final TextView budgetDueDate;
        final TextView budgetSticker;

        final CardView budgetCard;
        final CardView budgetStickerCard;
        final CardView deleteContent;

        final ProgressBar budgetProgression;
        final TextView consumedPercentageValue;

        public BudgetViewHolder(@NonNull View budgetView) {
            super(budgetView);

            budgetTitle = budgetView.findViewById(R.id.budget_title);
            budgetLogoView = budgetView.findViewById(R.id.title_char_indicator);
            budgetAmount = budgetView.findViewById(R.id.budget_amount);
            budgetDebutDate = budgetView.findViewById(R.id.beggining_date);
            budgetDueDate = budgetView.findViewById(R.id.due_date);
            budgetSticker = budgetView.findViewById(R.id.sticker);
            budgetStickerCard = budgetView.findViewById(R.id.budget_sticker);
            budgetCard = budgetView.findViewById(R.id.budget_card);
            deleteContent = budgetView.findViewById(R.id.delete_content);

            budgetProgression = budgetView.findViewById(R.id.budget_consumption_progress);
            consumedPercentageValue = budgetView.findViewById(R.id.consumed_value);


        }

    }
}
