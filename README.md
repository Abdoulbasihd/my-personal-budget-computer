# my-personal-budget-computer
Fix a periodical budget, compute daily spends


## 1.  Fonctionnalités

En gros, nous pouvons :
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
Le budget a donc comme elt calculé le montant consommé
- Le budget dont le montant a été totalement consommé ne devraient plus être selectionnable pour une dépense donnée
- Une dépense est associée à un et un seul budget. et lorsque ledit budget est supprimé, toutes les dépenses associées le sont également
- Pour ajouter une dépense, on doit pouvoir selectionner le budget associé et vérifier que le budget selectionné le permet (montant non totalement consommé)


## 2. Les enregistrements

* > __**Budget**__
	 - denomination ou intitulé
	 - date de début
	 - date de fin
	 - montant total
	 - [liste des dépenses lié. relation 1 budget pour plusieurs depenses ]
	 - sticker
	 - description

* > __**depense**__
	 - intitulé
	 - montant
	 - date et heure de dépense
	 - raison (description)
	 - sticker

* > __**Gain**__
	 - intitulé
	 - montant
	 - date de gain
	 - source des fonds
	 - description
	 - sticker

* > __**Dette**__
	 - intitulé
	 - montant
	 - date d'emprunt
	 - date d'échéance de remboursement (date au plus tard)
	 - date de remboursement (date effectif de remboursement)
	 - remboursé ou non (boolean)
	 - nom du créancier
	 - contact du créancier
	 - sticker
	 - temoin
	
* > __**Creance**__
	 - intitulé
	 - montant
	 - date de prêt
	 - date d'échéance de remboursement
	 - date de remboursement
	 - nom de l'emprunteur
	 - contact de l'emprunteur
	 - description
	 - sticker
	 - temoin


* > __**Account**__
	- nom du compte
	- créance utilisé
	- balance liquide
	- balance en bank
	- balance en porte feuille mobile (mobile money)
	- total balance => calculé
	- budgetized balance
	
___________________________________________________________
La création de budget dépend du compte. Pour la prémière version de l'application, l'utilisateur a un seul compte (Pour les prochaines versions, on peut prévoir des sous comptes). Un budget est donc lié à un compte utilisateur et un compte utilisateur peut avoir plusieurs budget
Pour la création de budget, on devrait vérifier que le compte a de l'argent suffisemment. Pour celà, l'on doit être en mesure de determiner le montant non budgétisé

Lors de la création d'une dépense, on débite le montant du compte

