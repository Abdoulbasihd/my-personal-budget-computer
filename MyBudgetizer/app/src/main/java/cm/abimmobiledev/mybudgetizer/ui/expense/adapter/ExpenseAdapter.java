package cm.abimmobiledev.mybudgetizer.ui.expense.adapter;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.database.BudgetizerAppDatabase;
import cm.abimmobiledev.mybudgetizer.database.entity.Expense;
import cm.abimmobiledev.mybudgetizer.useful.Util;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private final List<Expense> expenses;

    public ExpenseAdapter(List<Expense> expenses) {
        this.expenses = expenses;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View expenseElementView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);

        return new ExpenseViewHolder(expenseElementView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);

        holder.expenseReason.setText(expense.getReason());
        holder.expenseTitle.setText(expense.getEntitled());
        holder.expenseAmount.setText(String.valueOf(expense.getAmount()));
        holder.expenseDate.setText(expense.getDateTimeOfExpense());

        holder.expenseCard.setOnClickListener(v -> Toast.makeText(holder.expenseCard.getContext(), "suggestions @ abdulbasihd@gmail.com. Thanks", Toast.LENGTH_LONG).show());
        
        holder.deleteContent.setOnClickListener(deleteItemView -> {

            ProgressDialog expenseDelProgress = Util.initProgressDialog(holder.deleteContent.getContext(), holder.deleteContent.getContext().getString(R.string.deleting));
            expenseDelProgress.show();

            AlertDialog.Builder myExpenseDeletorDialog = Util.initAlertDialogBuilder(holder.deleteContent.getContext(), "État",  "Dépense Supprimée. Bien vouloir actualiser");


            ExecutorService expenseDelService = Executors.newSingleThreadExecutor();
            
            expenseDelService.execute(() -> {

                BudgetizerAppDatabase expenseDelAppDatabase = BudgetizerAppDatabase.getInstance(holder.deleteContent.getContext());
                expenseDelAppDatabase.expenseDAO().delete(expense);
                expenses.remove(expense);

                new Handler(Looper.getMainLooper()).post(() -> {
                    expenseDelProgress.dismiss();
                    myExpenseDeletorDialog.setPositiveButton("OK", (dialog, which) -> {
                        //nothing to do here, just close
                    }).show();
                });



                notifyItemRemoved(position);
                notifyItemChanged(position);
                //notifyItemPositionChanged(position, expenses.size());

            });
        });
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        final TextView expenseTitle;
        final TextView expenseAmount;
        final TextView expenseDate;
        final TextView expenseReason;
        final CardView expenseCard;
        final CardView deleteContent;

        public ExpenseViewHolder(@NonNull View expenseItemView) {
            super(expenseItemView);

            expenseTitle = expenseItemView.findViewById(R.id.expense_title);
            expenseAmount = expenseItemView.findViewById(R.id.amount_expended);
            expenseDate = expenseItemView.findViewById(R.id.expense_date);
            expenseReason = expenseItemView.findViewById(R.id.expense_reason);

            expenseCard = expenseItemView.findViewById(R.id.expense_card);
            deleteContent = expenseItemView.findViewById(R.id.delete_content);

        }

    }


    public int notifyItemPositionChanged(int position, int length) {

        if (position<0 || length <0 || position>length)
            return 0;

        for (int counter = position; counter < length; counter++) {
            notifyItemChanged(counter);
            Log.d("TAG", "notifyItemPositionChanged: "+counter);
        }

        return 1;
    }
}
