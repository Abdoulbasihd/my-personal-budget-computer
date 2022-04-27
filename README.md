# my-personal-budget-computer
Fix a periodical budget, compute daily spends


## 1.  Fonctionnalités

* --> ajouter, consulter, modifier, supprimer une dépense (crud)
* --> ajouter, consulter, modifier, supprimer une dette (crud)
* --> ajouter, consulter, modifier, supprimer un gain (crud)
* --> ajouter, consulter, modifier, supprimer une créance (crud)  (ce qui m'est dû)
* --> ajouter, consulter, modifier, supprimer un budget
* --> lister/consulter mes dépenses pour une période (jour, mois, année)
* --> lister/consulter mes gains pour une période
* --> lister/consulter mes dettes  d'une période
* --> lister/consulter mes créance d'une période
* --> lister/consulter toutes mes dettes non remboursée
* --> lister/consulter mes dettes remboursée d'une période
* --> lister/consulter toutes mes créances non payées
* --> lister/consulter mes créances payées d'une période


__***Une dépense peut être liée ou non à un budget. Et un budget peut enregistrer plusieurs dépenses***__

- L'affichage de la liste des budget doit pouvoir indiquer le niveau de consommation du budget.
Par exemple, le budget peut être consommé à 30% à date d'affichage.
- Le budget dont le montant a été totalement consommé ne devraient plus être selectionnable pour une dépense donnée
- Quid de budget expiré et non totalement consommé ? que faire de ca ?

## 2. Les enregistrements

* > __**Budget**__
	 - denomination ou intitulé
	 - date de début
	 - date de fin
	 - montant total
	 - [liste des dépenses lié ? relation 1 budget pour plusieur depenses ]

* > __**depense**__
	 - intitulé
	 - montant
	 - date et heure de dépense
	 - raison (description)

* > __**Gain**__
	 - intitulé
	 - montant
	 - date de gain
	 - source des fonds
	 - description

* > __**Dette**__
	 - intitulé
	 - montant
	 - date d'emprunt
	 - date d'échéance de remboursement (date au plus tard)
	 - date de remboursement (date effectif de remboursement)
	 - remboursé ou non (boolean)
	 - nom du créancier
	 - contact du créancier
	
* > __**Creance**__
	 - intitulé
	 - montant
	 - date de prêt
	 - date d'échéance de remboursement
	 - date de remboursement
	 - nom de l'emprunteur
	 - contact de l'emprunteur
	 - description


__Add next : link to an account__

