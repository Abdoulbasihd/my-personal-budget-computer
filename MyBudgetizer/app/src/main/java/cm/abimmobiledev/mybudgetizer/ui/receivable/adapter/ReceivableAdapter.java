package cm.abimmobiledev.mybudgetizer.ui.receivable.adapter;

import static cm.abimmobiledev.mybudgetizer.ui.earning.EarningRegistrationActivity.updateCorrectWallet;
import static cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseDashboardActivity.getCurrentDayFormatted;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import cm.abimmobiledev.mybudgetizer.database.entity.Receivable;
import cm.abimmobiledev.mybudgetizer.useful.Util;

public class ReceivableAdapter extends RecyclerView.Adapter<ReceivableAdapter.ReceivableViewHolder> {

    private final List<Receivable> receivables;

    public ReceivableAdapter(List<Receivable> receivables) {
        this.receivables = receivables;
    }

    @NonNull
    @Override
    public ReceivableAdapter.ReceivableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View receivableView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_receivable, parent, false);
        return new ReceivableAdapter.ReceivableViewHolder(receivableView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceivableAdapter.ReceivableViewHolder holder, int position) {

        Receivable receivable = receivables.get(position);

        if (receivable.isRefundedOrPaid()){
            holder.unpaidCard.setVisibility(View.GONE);
            holder.paidCard.setVisibility(View.VISIBLE);
        }
        else {
            holder.unpaidCard.setVisibility(View.VISIBLE);
            holder.paidCard.setVisibility(View.GONE);
        }

        if (receivable.getSticker().isEmpty())
            holder.receivableStickerCard.setVisibility(View.GONE);
        else
            holder.receivableStickerCard.setVisibility(View.VISIBLE);

        holder.receivableSticker.setText(receivable.getSticker());
        holder.debtorName.setText(receivable.getDebtorName());
        holder.debtorContact.setText(receivable.getDebtorContact());
        holder.telltaleName.setText(receivable.getTelltale());
        holder.paidOnDate.setText(receivable.getDisbursementDate());
        holder.dueDate.setText(receivable.getDueDate());
        holder.loanDate.setText(receivable.getLoanDate());
        holder.receivableAmount.setText(String.valueOf(receivable.getAmount()));
        holder.receivableTitle.setText(receivable.getEntitled());

        if (receivable.getEntitled()!=null && receivable.getEntitled().length()>0){
            holder.logoView.setText(String.valueOf(receivable.getEntitled().toUpperCase(Locale.ROOT).charAt(0)));
        }

        if (receivable.isRefundedOrPaid())
            holder.editCard.setVisibility(View.GONE);

        holder.editCard.setOnClickListener(v -> {
            AlertDialog.Builder myRecUpdateAlertDialog = Util.initAlertDialogBuilder(holder.debtCard.getContext(), "Modification",  "Voulez-vous modifier ou marquer comme payée ?");
            AlertDialog.Builder myRecUpdatedAlertDialog = Util.initAlertDialogBuilder(holder.debtCard.getContext(), holder.debtCard.getContext().getString(R.string.state),  "Effectuée ! \nBien vouloir recharger la page...");

            myRecUpdateAlertDialog.setNegativeButton("Marquer payée aujourd'hui", (dialog, which) -> {
                //Ne rien faire ici
                if (receivable.isRefundedOrPaid()){
                    Toast.makeText(v.getContext(),v.getContext().getString( R.string.already_paid), Toast.LENGTH_SHORT).show();
                    return;
                }
                String currentDay = getCurrentDayFormatted(Calendar.getInstance());
                receivable.setDisbursementDate(currentDay);
                receivable.setRefundedOrPaid(true);

                ProgressDialog debtUpdateProgress = Util.initProgressDialog(holder.debtCard.getContext(), "En cours");
                debtUpdateProgress.show();


                ExecutorService debtUpdateExecService = Executors.newSingleThreadExecutor();
                debtUpdateExecService.execute(() -> {
                    BudgetizerAppDatabase appDatabaseDebtUpdate = BudgetizerAppDatabase.getInstance(holder.receivableSticker.getContext());
                    //lorsqu'on paie une dette, notre compte est décrémenté.... Quel sous compte impacter alors.... par défaut nous choisissons le compte cash
                    Account acc =
                            updateCorrectWallet(
                                    appDatabaseDebtUpdate.accountDAO().getAccounts().get(0),
                                    receivable.getAmount(),
                                    0
                            );
                    appDatabaseDebtUpdate.receivableDAO().update(receivable);
                    appDatabaseDebtUpdate.accountDAO().update(acc);

                    new Handler(Looper.getMainLooper()).post(() -> {
                        debtUpdateProgress.dismiss();
                        myRecUpdatedAlertDialog.setPositiveButton("OK", (dialog1, which1) -> {
                            //nothing to do here, just close
                        }).show();
                    });
                });
            });
            myRecUpdateAlertDialog.setPositiveButton("Plus de modification", (dialog, which) -> {
                //TODO : open new activity for updating. could be the same as registration page
            });
            myRecUpdateAlertDialog.show();


        });


        holder.deleteContent.setOnClickListener(deleteItemView -> {

            AlertDialog.Builder myRecAlertDeletionDialog = Util.initAlertDialogBuilder(holder.deleteContent.getContext(), holder.deleteContent.getContext().getString(R.string.state),  "Voulez-vous vraiment supprimer ?");
            myRecAlertDeletionDialog.setNegativeButton("Annuler", (dialog, which) -> {
                //Ne rien faire ici
            });
            myRecAlertDeletionDialog.setPositiveButton("Continuer", (dialog, which) -> {
                ProgressDialog recDelProgress = Util.initProgressDialog(holder.deleteContent.getContext(), holder.deleteContent.getContext().getString(R.string.deleting));
                recDelProgress.show();

                AlertDialog.Builder myRecDeletorDialog = Util.initAlertDialogBuilder(holder.deleteContent.getContext(), holder.deleteContent.getContext().getString(R.string.state),  "Créance Supprimée. Bien vouloir actualiser");


                ExecutorService recDelService = Executors.newSingleThreadExecutor();
                recDelService.execute(() -> {
                    BudgetizerAppDatabase recDelAppDatabase = BudgetizerAppDatabase.getInstance(holder.deleteContent.getContext());
                    recDelAppDatabase.receivableDAO().delete(receivable);
                    receivables.remove(receivable);

                    new Handler(Looper.getMainLooper()).post(() -> {
                        recDelProgress.dismiss();
                        myRecDeletorDialog.setPositiveButton("Ok", (dialog1, which1) -> {
                            //nothing to do here, just close
                        }).show();
                    });



                    notifyItemRemoved(position);
                    notifyItemChanged(position);

                });
            });
            myRecAlertDeletionDialog.show();


        });

    }

    @Override
    public int getItemCount() {
        return receivables.size();
    }

    public static class ReceivableViewHolder extends RecyclerView.ViewHolder {

        final TextView receivableTitle;
        final TextView receivableAmount;
        final TextView loanDate;
        final TextView dueDate;
        final TextView paidOnDate;
        final TextView debtorName;
        final TextView debtorContact;
        final TextView logoView;

        final TextView telltaleName; //the person who testify

        final CardView debtCard;
        final CardView deleteContent;
        final CardView editCard;

        final TextView receivableSticker;
        final CardView receivableStickerCard;

        final CardView paidCard;
        final CardView unpaidCard;

        public ReceivableViewHolder(@NonNull View itemView) {
            super(itemView);

            receivableTitle = itemView.findViewById(R.id.receivable_title);
            receivableAmount = itemView.findViewById(R.id.amount);
            loanDate = itemView.findViewById(R.id.loan_date);
            dueDate = itemView.findViewById(R.id.due_date);
            paidOnDate = itemView.findViewById(R.id.pay_date);
            debtorName = itemView.findViewById(R.id.debtor_names);
            debtorContact = itemView.findViewById(R.id.debtor_contact);
            telltaleName = itemView.findViewById(R.id.witness_name);

            debtCard = itemView.findViewById(R.id.receivable_card);
            deleteContent = itemView.findViewById(R.id.delete_content);
            editCard = itemView.findViewById(R.id.more_action_card);

            receivableSticker = itemView.findViewById(R.id.sticker);
            receivableStickerCard = itemView.findViewById(R.id.receivable_sticker);

            paidCard = itemView.findViewById(R.id.receivable_paid_card);
            unpaidCard = itemView.findViewById(R.id.receivable_unpaid_card);

            logoView = itemView.findViewById(R.id.title_char_indicator);
        }
    }

}
