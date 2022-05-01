package cm.abimmobiledev.mybudgetizer.ui.debt.adapter;

import static cm.abimmobiledev.mybudgetizer.ui.earning.EarningRegistrationActivity.updateCorrectWallet;
import static cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseDashboardActivity.getCurrentDayFormatted;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.database.BudgetizerAppDatabase;
import cm.abimmobiledev.mybudgetizer.database.entity.Account;
import cm.abimmobiledev.mybudgetizer.database.entity.Debt;
import cm.abimmobiledev.mybudgetizer.useful.Util;

public class DebtAdapter extends RecyclerView.Adapter<DebtAdapter.DebtViewHolder> {

    private final List<Debt> debts;

    public DebtAdapter(List<Debt> debts) {
        this.debts = debts;
    }

    @NonNull
    @Override
    public DebtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View debtView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_debt, parent, false);
        return new DebtViewHolder(debtView);
    }

    @Override
    public void onBindViewHolder(@NonNull DebtViewHolder holder, int position) {

        Debt debt = debts.get(position);

        if (debt.isRefundedOrPaid()){
            holder.unpaidCard.setVisibility(View.GONE);
            holder.paidCard.setVisibility(View.VISIBLE);
        }
        else {
            holder.unpaidCard.setVisibility(View.VISIBLE);
            holder.paidCard.setVisibility(View.GONE);
        }

        if (debt.getSticker().isEmpty())
            holder.debtStickerCard.setVisibility(View.GONE);
        else
            holder.debtStickerCard.setVisibility(View.VISIBLE);

        holder.debtSticker.setText(debt.getSticker());
        holder.creditorName.setText(debt.getCreditorName());
        holder.creditorContact.setText(debt.getCreditorContact());
        holder.telltaleName.setText(debt.getTelltale());
        holder.paidOnDate.setText(debt.getRepaymentDate());
        holder.dueDate.setText(debt.getRepaymentDueDate());
        holder.loanDate.setText(debt.getLoaningDate());
        holder.debtAmount.setText(String.valueOf(debt.getAmount()));
        holder.debtTitle.setText(debt.getEntitled());

        if (debt.getEntitled()!=null && debt.getEntitled().length()>0){
            holder.debtLogoView.setText(String.valueOf(debt.getEntitled().toUpperCase(Locale.ROOT).charAt(0)));
        }

        holder.editCard.setOnClickListener(v -> {
            AlertDialog.Builder myDebtUpdateAlertDialog = Util.initAlertDialogBuilder(holder.debtCard.getContext(), "Modification",  "Voulez-vous modifier ou marquer comme payée ?");
            AlertDialog.Builder myDebtUpdatedAlertDialog = Util.initAlertDialogBuilder(holder.debtCard.getContext(), holder.debtCard.getContext().getString(R.string.state),  "Effectuée ! \nBien vouloir recharger la page...");

            myDebtUpdateAlertDialog.setNegativeButton("Marquer payée aujourd'hui", (dialog, which) -> {
                //Ne rien faire ici
                String currentDay = getCurrentDayFormatted(Calendar.getInstance());
                debt.setRepaymentDate(currentDay);
                debt.setRefundedOrPaid(true);

                ProgressDialog debtUpdateProgress = Util.initProgressDialog(holder.debtCard.getContext(), "En cours");
                debtUpdateProgress.show();


                ExecutorService debtUpdateExecService = Executors.newSingleThreadExecutor();
                debtUpdateExecService.execute(() -> {
                    BudgetizerAppDatabase appDatabaseDebtUpdate = BudgetizerAppDatabase.getInstance(holder.debtSticker.getContext());
                    //lorsqu'on paie une dette, notre compte est décrémenté.... Quel sous compte impacter alors.... par défaut nous choisissons le compte cash
                    Account acc =
                            updateCorrectWallet(
                                    appDatabaseDebtUpdate.accountDAO().getAccounts().get(0),
                                    debt.getAmount(),
                                    0
                            );
                    appDatabaseDebtUpdate.debtDAO().update(debt);
                    appDatabaseDebtUpdate.accountDAO().update(acc);

                    new Handler(Looper.getMainLooper()).post(() -> {
                        debtUpdateProgress.dismiss();
                        myDebtUpdatedAlertDialog.setPositiveButton("OK", (dialog1, which1) -> {
                            //nothing to do here, just close
                        }).show();
                    });
                });
            });
            myDebtUpdateAlertDialog.setPositiveButton("Plus de modification", (dialog, which) -> {
                //TODO : open new activity for updating. could be the same as registration page
            });
            myDebtUpdateAlertDialog.show();


        });


        holder.deleteContent.setOnClickListener(deleteItemView -> {

            AlertDialog.Builder myDebtAlertDeletionDialog = Util.initAlertDialogBuilder(holder.deleteContent.getContext(), holder.deleteContent.getContext().getString(R.string.state),  "Voulez-vous vraiment supprimer ?");
            myDebtAlertDeletionDialog.setNegativeButton("Annuler", (dialog, which) -> {
                //Ne rien faire ici
            });
            myDebtAlertDeletionDialog.setPositiveButton("Continuer", (dialog, which) -> {
                ProgressDialog debtDelProgress = Util.initProgressDialog(holder.deleteContent.getContext(), holder.deleteContent.getContext().getString(R.string.deleting));
                debtDelProgress.show();

                AlertDialog.Builder myDebtDeletorDialog = Util.initAlertDialogBuilder(holder.deleteContent.getContext(), holder.deleteContent.getContext().getString(R.string.state),  "Dette Supprimée. Bien vouloir actualiser");


                ExecutorService debtDelService = Executors.newSingleThreadExecutor();
                debtDelService.execute(() -> {
                    BudgetizerAppDatabase debtDelAppDatabase = BudgetizerAppDatabase.getInstance(holder.deleteContent.getContext());
                    debtDelAppDatabase.debtDAO().delete(debt);
                    debts.remove(debt);

                    new Handler(Looper.getMainLooper()).post(() -> {
                        debtDelProgress.dismiss();
                        myDebtDeletorDialog.setPositiveButton("Ok", (dialog1, which1) -> {
                            //nothing to do here, just close
                        }).show();
                    });



                    notifyItemRemoved(position);
                    notifyItemChanged(position);

                });
            });
            myDebtAlertDeletionDialog.show();


        });

    }

    @Override
    public int getItemCount() {
        return debts.size();
    }

    public static class DebtViewHolder extends RecyclerView.ViewHolder {

        final TextView debtTitle;
        final TextView debtAmount;
        final TextView loanDate;
        final TextView dueDate;
        final TextView paidOnDate;
        final TextView creditorName;
        final TextView creditorContact;

        final TextView telltaleName; //the person who testify

        final CardView debtCard;
        final CardView deleteContent;
        final CardView editCard;

        final TextView debtSticker;
        final CardView debtStickerCard;

        final CardView paidCard;
        final CardView unpaidCard;

        final TextView debtLogoView;

        public DebtViewHolder(@NonNull View itemView) {
            super(itemView);

            debtTitle = itemView.findViewById(R.id.debt_title);
            debtAmount = itemView.findViewById(R.id.amount);
            loanDate = itemView.findViewById(R.id.loan_date);
            dueDate = itemView.findViewById(R.id.due_date);
            paidOnDate = itemView.findViewById(R.id.pay_date);
            creditorName = itemView.findViewById(R.id.creditor_names);
            creditorContact = itemView.findViewById(R.id.creditor_contact);
            telltaleName = itemView.findViewById(R.id.witness_name);

            debtCard = itemView.findViewById(R.id.debt_card);
            deleteContent = itemView.findViewById(R.id.delete_content);
            editCard = itemView.findViewById(R.id.more_action_card);

            debtSticker = itemView.findViewById(R.id.sticker);
            debtStickerCard = itemView.findViewById(R.id.debt_sticker);

            paidCard = itemView.findViewById(R.id.debt_paid_card);
            unpaidCard = itemView.findViewById(R.id.debt_unpaid_card);

            debtLogoView = itemView.findViewById(R.id.title_char_indicator);
        }
    }
}
