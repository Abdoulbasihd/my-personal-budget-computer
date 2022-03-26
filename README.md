# my-personal-budget-computer
Fix a periodical budget, compute daily spends


## 1.  Fonctionnalités

* --> ajouter, modifier, supprimer une dépense (crud)
* --> ajouter, modiifer, supprimer une dette (crud)
* --> ajouter, modifier, supprimer un gain (crud)
* --> ajouter, modifier, supprimer une créance (crud)  (ce qui m'est dû)
* --> ajouter, modifier, supprimer un budget
* --> lister mes dépenses pour une période (jour, mois, année)
* --> lister mes gains pour une période
* --> lister mes dettes  d'une période
* --> lister mes créance d'une période
* --> lister toutes mes dettes non remboursée
* --> lister mes dettes remboursée d'une période
* --> lster toutes mes créances non payées
* --> lister mes créances payées d'une période


__***Une dépense peut être liée ou non à un budget. Et un budget peut enregistrer plusieurs dépenses***__


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
	 - date d'échéance de remboursement
	 - date de remboursement
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

