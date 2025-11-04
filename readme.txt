


Cette version remplace tous les tableaux de byte par des Pair.
On insere des checks d'accès avant chaque instruction BALOAD/BASTORE.

Pour tester avec la chaine de 3 serveurs (LB, INTER, WEB) : 

make compile

Dans 3 terminaux différents :

make web
make inter
make lb

dans un navigateur : localhost:2003/image.png

vérification:
- dans la fenêtre web, il y a :
MyOutputStream.write: virtual local (2097416) // la payload a été envoyée en virtuel
Downloader: provide payload (2097416) // la payload a été downloadée
- dans la fenêtre inter :
MyInputStream.read: virtual payload(2097416) // on a reçu la payload virtuelle
MyOutputStream.write: virtual remote (2097416) // on a propagé la payload en virtuel
- dans la fenêtre lb
MyInputStream.read: virtual payload(2097416) // on a reçu la payload virtuelle 
MyOutputStream.write: download payload (2097416) // on a downloadé la payload pour l'envoyer au client


########################

TODO

faire un downloader unique

optimiser le download (pas de serialisation, descripteurs)

que se passe t il si après un write virtual, l'application accède au buffer ?
Après le write, on garde le buffer protégé. Si on faute, on downloade, sinon, on réutilise le buffer lors du prochainread sur socket.

On aurait envie de faire :
- MySocket hérite de Socket, ServerSocket hérite de MyServerSocket, MyOutputStream hérite de OutputStream, MyInputStream hérite de InputStream
- l'avantage est qu'on ne change pas le type des champs et variables dans le code (on garde Socket, InputStream ...)
- l'avantage est aussi que quand on utilise un FileInputStream qui hérite de InputStream, on n'instrumente pas ce code et ça marche
- ça marche avec la version Tab[0] ou les références de buffer sont toujours de type [B mais ça ne marche pas avec des Pair
	- car MyOutputStream prend des Pair en paramètres et pas OutputStream
	- donc on doit surcharger FileInputStream en MyFileInputStream pour qu'il prennent des Pair en paramètre
	
	
