<HTML>
<head>
<title>Aide de Full Metal Galaxy</title>
        
<%@include file="/include/meta.jsp"%>

<meta name='gwt:property' id='app_history' content='loginbtn__newlogin__'>

</head>
<body >

<%@include file="/include/header.jsp"%>

<%@include file="menu.html"%>

    <h1>Les r&egrave;gles du jeu</h1>
    
			<P STYLE="margin-bottom: 0.5cm"><FONT FACE="Verdana, sans-serif">La
			r&egrave;gle suivante correspond au jeu original, je l'adapterai
			au jeu en ligne d'ici peu. On peut cependant noter les diff&eacute;rences
			suivantes entre le jeu original et cette version en ligne :</FONT></P>
			<ul>
			  <li>Pas de d&eacute;ploiement gratuit (en cours)</li>
			  <li>Pas de limite du nombre de pi&egrave;ce dans la construction (en cours)</li>
			</ul>
			La notion de tour de jeu est facultative (voir mode asynchrone). Dans ce cas, tous 
			les joueurs peuvent jouer en même temps. Ceci entraine la modification des règles
			suivantes :
			<ul>
			  <li>La r&egrave;gle &quot;entre deux tours, deux chars ne peuvent pas	stationner 
			c&ocirc;te &agrave; c&ocirc;te sur des montagnes.&quot;
			a &eacute;t&eacute; adapt&eacute;e &agrave; &quot;si deux chars, du m&ecirc;me
			propri&eacute;taire, sont c&ocirc;te &agrave; c&ocirc;te sur des montagnes, l'un des
			deux ne produit pas de zone de feu&quot;
			</li>
			<li>Neutralisation tournante: la neutralisation est automatique et n'empêche pas de se déplacer. 
			En revanche, 
			un pion en mouvement ne créer pas de zone de feu (un pion  ne peut donc pas pénétrer 
			dans la zone de feu du pion qu'il neutralise).</li>
			</ul>
			<HR/>
			
			<P STYLE="margin-bottom: 0.5cm"><FONT FACE="Verdana, sans-serif"><FONT SIZE=4><B>LA
			PLANETE</B></FONT></FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=3><B>Le plateau</B></FONT><FONT SIZE=2><BR>Il
			est compos&eacute; de 851 cases hexagonales communiquant entre
			elles : (les demi-cases dans le sens de la longueur ne comptent
			pas).<BR>Les lignes pointill&eacute;es d&eacute;limitent les zones
			d'arriv&eacute;e des astronefs.<BR>La haute mer est bleue, la
			plaine est ocre, les r&eacute;cifs sont bleus tachet&eacute;s
			d'ocre, les mar&eacute;cages sont ocres tachet&eacute;s de bleu,
			la montagne est grise. La nature de chaque case d&eacute;pend de
			sa couleur dominante ; en cas d'h&eacute;sitation r&eacute;f&eacute;rez
			vous &agrave; l'atlas.</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=3><B>Le minerai</B></FONT><FONT SIZE=2><BR>Le
			minerai est diss&eacute;min&eacute; sur tout le plateau (voir
			d&eacute;marrage d'une partie). Il peut &ecirc;tre embarqu&eacute;
			d&eacute;finitivement dans les astronefs pour accumuler les points
			gagnants, ou &ecirc;tre transform&eacute; par la pondeuse m&eacute;t&eacute;o
			en pi&egrave;ces suppl&eacute;mentaires.<BR>Un pion minerai occupe
			une case, il est donc un obstacle au d&eacute;placement.<BR>A
			mar&eacute;e basse, le minerai est prenable partout. A mar&eacute;e
			normale, le minerai est imprenable sur les r&eacute;cifs. A mar&eacute;e
			haute. le minerai est imprenable sur les r&eacute;cifs et
			mar&eacute;cages.<BR>Les cinq mani&egrave;res d'agir sur le
			minerai sont</FONT></P>
			<UL>
				<LI><P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>le charger dans
				l'astronef &agrave; partir d'une case contig&uuml;e.</FONT> 
				</P>
				<LI><P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>le charger, le
				transporter, le d&eacute;charger au moyen d'un crabe (maximum 2
				minerais) ou d'une barge (maximum 4).</FONT> 
				</P>
				<LI><P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>charger,
				transporter, d&eacute;charger, ou transformer ce minerai au moyen
				d'une pondeuse-m&eacute;t&eacute;o (un seul minerai &agrave; la
				fois).</FONT> 
				</P>
				<LI><P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>le d&eacute;truire
				au moyen de deux destructeurs (char, vedette, tourelle, gros
				tas). </FONT>
				</P>
			</UL>
			
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=3><B>Les mar&eacute;es</B></FONT><FONT SIZE=2><BR>Chaque
			tour de jeu complet est soumis &agrave; une mar&eacute;e :
			normale, basse ou haute.<BR>La gestion des 15 cartes mar&eacute;es
			(cinq normales, cinq basses et cinq hautes) sera expliqu&eacute;e
			dans le chap&icirc;tre </FONT><FONT SIZE=2><I>d&eacute;marrage
			d'une partie</I></FONT><FONT SIZE=2>.</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>A mar&eacute;e
			normale, mar&eacute;cages = plaine, r&eacute;cifs = mer<BR>A mar&eacute;e
			haute, mar&eacute;cages et r&eacute;cifs = mer<BR>A mar&eacute;e
			basse, mar&eacute;cages et r&eacute;cifs = plaine</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2><I>Cons&eacute;quences</I><BR>A
			mar&eacute;e basse, les cases r&eacute;cifs sont infranchissables
			par les engins marins (vedettes et barges), et les engins
			terrestres y ont acc&egrave;s.<BR>A mar&eacute;e haute, les cases
			mar&eacute;cages sont infranchissables par les engins terrestres
			et les engins marins y ont acc&egrave;s.<BR>A mar&eacute;e
			normale, les mar&eacute;cages restent de la plaine, les r&eacute;cifs
			restent de la mer : les mar&eacute;cages sont praticables par les
			engins terrestres, et les r&eacute;cifs par les engins marins.</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>Les engins surpris
			par le changement de mar&eacute;e sont neutralis&eacute;s.<BR>Un
			engin marin surpris &agrave; mar&eacute;e basse sur une case r&eacute;cif
			ou mar&eacute;cage est &eacute;chou&eacute;.<BR>Un engin marin
			surpris &agrave; mar&eacute;e normale sur une case mar&eacute;cage
			est &eacute;chou&eacute;<BR>Un engin terrestre surpris &agrave;
			mar&eacute;e normale sur les r&eacute;cifs est embourb&eacute;.<BR>Un
			engin terrestre surpris &agrave; mar&eacute;e haute sur une case
			r&eacute;cif ou mar&eacute;cage est embourb&eacute;.</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>Les pi&egrave;ces
			ainsi surprises ne peuvent rien faire, absolument rien. Elles sont
			strictement inamovibles, mais restent capturables, destructibles,
			et constituent des obstacles &agrave; la circulation, bref elles
			sont neutralis&eacute;es jusqu'au retour d'une mar&eacute;e
			favorable.<BR>Il est possible d'&eacute;chouer volontairement un
			engin marin ou d'embourber volontairement un engin terrestre :
			dans ce cas, l'engin doit s'arr&ecirc;ter sur la premi&egrave;re
			case qui lui est momentan&eacute;ment impraticable, et se retrouve
			neutralis&eacute;. </FONT>
			</P>
			<P STYLE="margin-bottom: 0.5cm">&nbsp;</P>
			<HR>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=4><B>POINTS ET
			DEPLACEMENTS</B></FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=3><B>Cr&eacute;dit de
			base</B></FONT><FONT SIZE=2><BR>Lorsque arrive son tour, chaque
			joueur dispose d'un cr&eacute;dit de base de quinze points, qu'il
			d&eacute;pense comme il l'entend pour mener ses actions, et bouger
			autant de pi&egrave;ces qu'il le d&eacute;sire.</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=3><B>DEPENSER LES
			POINTS-ACTION</B></FONT> <FONT SIZE=2>D&eacute;placement</FONT> <FONT SIZE=2>un
			point pour chaque nouvelle case emprunt&eacute;e.</FONT>
			<FONT SIZE=2>Chargement ou</FONT> <FONT SIZE=2>le chargement ou
			d&eacute;chargement d'une pi&egrave;ce ou d'un minerai : un point.</FONT>
			<FONT SIZE=2>d&eacute;chargement</FONT> <FONT SIZE=2>chaque pi&egrave;ce
			ou minerai d&eacute;charg&eacute; co&ucirc;te un point.</FONT>
			<FONT SIZE=2>Construction cr&eacute;ation d'une pi&egrave;ce (&agrave;
			partir d'un minerai d&eacute;j&agrave; charg&eacute; sur la
			pondeuse) un point.</FONT> <FONT SIZE=2>Rentrer ou sortir</FONT>
			<FONT SIZE=2>rentrer ou sortir une pi&egrave;ce vide ou charg&eacute;e,
			de l'astronef : un point (y compris pour la barge qui occupe tout
			de suite deux cases).</FONT> <FONT SIZE=2>Tir</FONT> <FONT SIZE=2>destruction
			d'une pi&egrave;ce ou d'un pion-minerai : deux points (voir tirs).</FONT>
			<FONT SIZE=2>Reconstruction</FONT> <FONT SIZE=2>reconstruction
			d'une tourelle (seulement sur un astronef conquis) deux points.</FONT>
			<FONT SIZE=2>D&eacute;collage</FONT> <FONT SIZE=2>d&eacute;collage
			d'un astronef : un, deux, trois ou quatre points selon son &eacute;tat
			(voir d&eacute;part).</FONT> 
			</P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=3><B>Bonus</B></FONT><FONT SIZE=2><BR>Un
			joueur qui joue dix points sur ses quinze en &eacute;conomise
			cinq. Un joueur qui joue cinq points sur ses quinze en &eacute;conomise
			dix.<BR>L e maximum de points &eacute;conomisables est de dix,
			utilisable en totalit&eacute; ou par tranche de cinq, au tour de
			son choix.<BR>Chaque bonus de cinq points est mat&eacute;rialis&eacute;
			par un cube plac&eacute; visiblement devant le joueur
			int&eacute;ress&eacute;.<BR>le bonus permet donc &agrave; un
			joueur de jouer jusqu'&agrave; 25 points en un tour.<BR>Lorsqu'un
			bonus est utilis&eacute;, le ou les cubes correspondants sont
			remis dans le coffret. </FONT>
			</P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2><I>Super-bonus</I><BR>C
			haque astronef conquis augmente le cr&eacute;dit de base de
			l'occupant de cinq points (non mat&eacute;rialis&eacute; par un
			cube).</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2><I>Exemple</I> :<BR>le
			possesseur de trois astronefs (le sien propre plus deux conquis),
			qui aurait en outre &eacute;conomis&eacute; dix points, peut donc
			jouer cr&eacute;dit de base 25 points (15 + 5 + 5) + 10 points
			Total 35 points.</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><STRONG><FONT SIZE=3><B>Attention</B></FONT></STRONG><FONT SIZE=2><B><BR></B></FONT><FONT SIZE=2>Tout
			d&eacute;placement ou action est comptabilis&eacute;
			D&Eacute;FINITIVEMENT.<BR>Si une pi&egrave;ce effectue par erreur
			une manoeuvre impossible, seuls les points pr&eacute;c&eacute;dant
			cette manoeuvre sont comptabilis&eacute;s ; l'action de la pi&egrave;ce
			reprend &agrave; partir de la derni&egrave;re case avant la
			manoeuvre impossible.<BR>Sortie de l'astronef : chaque pi&egrave;ce,
			vide ou charg&eacute;e, occupe une case sur le plateau ; deux
			pi&egrave;ces ne peuvent jamais occuper la m&ecirc;me case (sauf
			pontons et transports). Seule la barge occupe deux cases ; chaque
			nouvelle case qu'elle occupe au cours d'un d&eacute;placement lui
			coute un point (marche avant ou marche arri&egrave;re). </FONT>
			</P>
			<P STYLE="margin-bottom: 0.5cm">&nbsp;</P>
			<HR/>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=4><B>LE TEMPS</B></FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>Chaque joueur
			dispose, &agrave; son tour de jeu, d'un maximum de trois minutes
			pour faire ses actions (munissez vous d'un chronom&egrave;tre) :
			il ne peut manipuler aucune pi&egrave;ce au del&agrave; de ce
			temps.<BR>Si un d&eacute;placement &eacute;tait en cours, la pi&egrave;ce
			manipul&eacute;e doit rester sur la case occup&eacute;e &agrave;
			la derni&egrave;re seconde.<BR>Les destructeurs peuvent achever
			leurs tirs ou prises s'ils occupaient avant la limite de temps,
			les positions n&eacute;cessaires.<BR>La fin du tour d'un joueur
			d&eacute;clenche le d&eacute;but du tour du joueur suivant.<BR>Le
			chronom&eacute;trage est assur&eacute;e par le joueur qui vient de
			finir son tour. Il doit indiquer au joueur chronom&eacute;tr&eacute;
			combien de temps il lui reste chaque fois que celui-ci en fait la
			demande.<BR>Tout joueur peut voir le chronom&egrave;tre.<BR>Il est
			tr&egrave;s important d'observer strictement cette r&egrave;gle
			des trois minutes, m&ecirc;me si elle occasionne des erreurs : la
			panique est un des ingr&eacute;dients de Full Metal Planete, et
			tout le monde y c&egrave;dera, ce n'est qu'une question de tours.
			Ne vous privez pas de ce plaisir. </FONT>
			</P>
			<P STYLE="margin-bottom: 0.5cm">&nbsp;</P>
			<HR>
			<P STYLE="margin-bottom: 0.5cm"><IMG SRC="images/regles_html_25762636.gif" NAME="Image2" ALIGN=RIGHT WIDTH=304 HEIGHT=343 BORDER=0><FONT SIZE=4><B>LES
			TRANSPORTEURS</B></FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=3><B>Barges, crabes</B></FONT>
			<FONT SIZE=2>- </FONT><FONT SIZE=3><B>chargement et
			d&eacute;chargement</B></FONT><FONT SIZE=2><BR>Il y a deux sortes
			de transporteurs :</FONT></P>
			<UL>
				<LI><P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>la </FONT><FONT SIZE=2><B>barge</B></FONT>
				<FONT SIZE=2>(contenance 4 places).</FONT> 
				</P>
				<LI><P STYLE="margin-bottom: 0.5cm"></P>
				<LI><P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>le </FONT><FONT SIZE=2><B>crabe</B></FONT>
				<FONT SIZE=2>(contenance 2 places).</FONT> 
				</P>
			</UL>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>Les &eacute;l&eacute;ments
			transportables sont : </FONT>
			</P>
			<UL>
				<LI><P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>le minerai, le
				char, le gros tas, le ponton, qui occupent chacun une place.</FONT>
								</P>
				<LI><P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>la pondeuse
				m&eacute;t&eacute;o et le crabe, qui occupent chacun deux places.</FONT>
								</P>
			</UL>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>Seule la barge peut
			transporter un crabe ou une pondeuse.<BR>Le chargement se fait
			depuis une case adjacente au transporteur. Le d&eacute;chargement
			se fait sur une case adjacente au transporteur.<BR>Le chargement
			ou de d&eacute;chargement d'un &eacute;l&eacute;ment coute un
			point.<BR>Embarquer ou d&eacute;barquer un crabe lui-m&ecirc;me
			rempli ne coute qu'un point, modifier son contenu &agrave; bord
			d'une barge ne co&ucirc;te pas de point.<BR>Toute pi&egrave;ce
			transport&eacute;e est neutralis&eacute;e.<BR>Une case de
			d&eacute;chargement doit &ecirc;tre vide, ou occup&eacute;e par un
			pont, ou par un autre transporteur sur lequel il reste de la
			place.<BR>Rentrer ou sortir un transporteur, m&ecirc;me charg&eacute;,
			de son astronef ne coute qu'un point.<BR>Un transporteur ne peut
			en aucun cas charger ou d&eacute;charger un &eacute;l&eacute;ment
			sur une case ou depuis une case situ&eacute;e sous le feu adverse
			(voir <I>destructeurs</I>).<BR>On peut charger puis d&eacute;charger
			un transporteur sans le d&eacute;placer.<BR>On ne peut rien
			d&eacute;charger en haute mer. On peut d&eacute;charger sur une
			case submerg&eacute;e (r&eacute;cit ou mar&eacute;cage) les
			&eacute;l&eacute;ments d&eacute;pos&eacute;s sont neutralis&eacute;s
			jusqu'&agrave; une mar&eacute;e favorable. On ne peut pas
			embarquer un &eacute;l&eacute;ment occupant une case submerg&eacute;e
			(sauf un ponton) </FONT>
			</P>
			<P STYLE="margin-bottom: 0.5cm">&nbsp;</P>
			<HR>
			<P STYLE="margin-bottom: 0.5cm"><IMG SRC="images/regles_html_41c28404.gif" NAME="Image3" ALIGN=LEFT WIDTH=304 HEIGHT=299 BORDER=0><FONT SIZE=4><B>LA
			PONDEUSE METEO</B></FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><BR><BR>
			</P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=3><B>Fabrication des
			pi&egrave;ces</B></FONT><FONT SIZE=2><BR>Une pondeuse m&eacute;t&eacute;o
			peut cr&eacute;er des nouvelles pi&egrave;ces elle s'arr&ecirc;te
			sur une case voisine d'un minerai, charge le minerai, et d&eacute;pose
			sur une case voisine un char, un pont ou un crabe pris dans la
			case </FONT><FONT SIZE=2><I>r&eacute;serve </I></FONT><FONT SIZE=2>:
			le pion-minerai utilis&eacute; retourne dans le coffret.<BR>L a
			cr&eacute;ation d'une nouvelle pi&egrave;ce coute donc deux points
			: un point pour le chargement du minerai, un point pour la pose de
			la pi&egrave;ce.<BR>Une pondeuse peut charger un minerai, se
			d&eacute;placer, et pondre sa pi&egrave;ce plus loin, voir &agrave;
			un autre tour.<BR>Lorsqu'elle charge ou pond, la pondeuse est
			soumise aux m&ecirc;mes r&egrave;gles qu'un transporteur.<BR>Une
			pondeuse peut cr&eacute;er deux pi&egrave;ces &agrave; chaque
			tour, elle ne peut pas cr&eacute;er deux crabes ou deux ponts dans
			le m&ecirc;me tour.<BR>Une pondeuse peut se contenter de
			transporter et d&eacute;charger un minerai (&agrave; ce titre,
			elle peut &ecirc;tre assimil&eacute;e &agrave; un transporteur).</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2><B>Attention</B><BR>La
			composition de la r&eacute;serve. suivant le nombre de
			participants, est con&ccedil;ue pour que chaque joueur ait en
			d&eacute;but de partie les m&ecirc;mes possibilit&eacute;s de
			cr&eacute;ation ; ensuite il s'agira pour chacun de faire ses
			choix : ramener le maximum de minerai dans l'astronef ou le
			transformer pour &ecirc;tre plus fort sur le terrain.</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=3><B>Pr&eacute;vision
			des mar&eacute;es</B></FONT><FONT SIZE=2><BR>Au moment du
			changement de mar&eacute;e. tout possesseur d'une pondeuse m&eacute;t&eacute;o
			op&eacute;rationnelle peut prendre connaissance de la mar&eacute;e
			future : un joueur poss&eacute;dant deux pondeuses m&eacute;t&eacute;o
			en &eacute;tat de marche peut conna&icirc;tre deux mar&eacute;es
			futures, etc. Une pondeuse m&eacute;t&eacute;o est inop&eacute;rante
			dans l'astronef, sur une barge, sous le feu adverse, ou embourb&eacute;e.
			</FONT>
			</P>
			<P STYLE="margin-bottom: 0.5cm">&nbsp;</P>
			<HR>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=4><B>LES PONTONS</B></FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>Les pontons, engins
			inertes, servent &agrave; couvrir les cases-haute mer et les cases
			submerg&eacute;es (ou submergeables).<BR>Un ponton se pose ou se
			retire &agrave; l'aide d'un transporteur (ou d'un astronef),
			quelle que soit la mar&eacute;e.<BR>Un ponton est utilisable
			exactement comme une case terre. Il doit toucher au moins une case
			terre. Il est possible de juxtaposer plusieurs pontons, si le
			premier ponton pos&eacute; touche une case terre. Dans ce cas, si
			ce premier ponton est d&eacute;truit ou retir&eacute;, le reste de
			l'assemblage est &eacute;galement d&eacute;truit. Si une case
			terre servant d'unique appui est submerg&eacute;e, le pont ou
			l'ensemble de ponts est d&eacute;truit. Un ponton d&eacute;truit
			entra&icirc;ne la destruction de tout ce qui se trouvait
			dessus.<BR>Sous le feu ennemi, on ne peut jamais poser ni retirer
			un ponton.<BR>Un ponton est neutre. Une fois pos&eacute;, il peut
			&ecirc;tre utilis&eacute; par tout &eacute;l&eacute;ment
			terrestre, quel que soit son camp. De m&ecirc;me, il peut &ecirc;tre
			charg&eacute; par un transporteur ennemi sans qu'il soit besoin de
			proc&eacute;der &agrave; sa capture (voir <I>capture</I>).<BR>Pos&eacute;
			sur une case terre, un ponton n'emp&ecirc;che pas l'usage de cette
			case. Un ponton fait obstacle &agrave; la circulation des barges
			et des vedettes. </FONT>
			</P>
			<P STYLE="margin-bottom: 0.5cm">&nbsp;</P>
			<HR>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=4><B>LES DESTRUCTEURS</B></FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>Les destructeurs
			peuvent d&eacute;truire tout &eacute;l&eacute;ment (pi&egrave;ce
			ou minerai), except&eacute;s les astronefs et leur contenu (les
			tourelles peuvent &ecirc;tre d&eacute;truites).<BR>Les
			destructeurs servent &agrave; d&eacute;truire, capturer, repousser
			l'adversaire, et &agrave; se d&eacute;fendre.</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>Il y a quatre types
			de destructeurs : les vedettes, les chars, le super-char T-99 &agrave;
			tir longue port&eacute;e, famili&egrave;rement surnomm&eacute;e &quot;
			Gros Tas &quot;, et les tourelles (fixes).</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><IMG SRC="images/regles_html_m7ba90e96.gif" NAME="Image4" ALIGN=LEFT WIDTH=308 HEIGHT=458 BORDER=0><FONT SIZE=3><B>Destruction</B></FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2><B>Principe de
			destruction</B><BR>Tout &eacute;l&eacute;ment atteint par le tir
			simultan&eacute; de deux destructeurs du m&ecirc;me joueur est
			d&eacute;truit et mis au cimeti&egrave;re.</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2><B>M&eacute;canisme
			de destruction</B><BR>Tourelle, char et vedette peuvent tirer
			jusqu'&agrave; deux cases de distance de leur propre position.<BR>Le
			gros tas peut atteindre une cible situ&eacute;e jusqu'&agrave;
			trois cases de sa propre position.<BR>Un char normal, situ&eacute;
			sur une case montagne, peut lui aussi tirer jusqu'&agrave; trois
			cases (m&ecirc;me sur une autre case montagne).<BR>Le gros tas,
			m&ecirc;me transport&eacute;, ne peut jamais aller sur une case
			montagne.<BR>Entre deux tours, deux chars ne peuvent pas
			stationner sur deux cases montagne adjacentes.<BR>Un destructeur
			peut tirer par dessus tout &eacute;l&eacute;ment ou case du
			plateau. y compris mer, minerai. astronef, montagne, etc.<BR>Un
			destructeur ne peut tirer que deux fois pendant le tour d'un
			joueur(ces tirs peuvent &ecirc;tre s&eacute;par&eacute;s par
			d'autres actions).<BR>Tout destructeur est r&eacute;approvisionn&eacute;
			en munitions d&egrave;s le d&eacute;but du tour du joueur
			suivant.<BR>Les destructeurs ne peuvent pas d&eacute;truire les
			pi&egrave;ces de leur propre camp sauf les pontons.<BR>Tirer sur
			une case entra&icirc;ne la destruction de tout ce qui occupait
			cette case.</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2><I><B>Rappel :</B></I>
			il faut toujours le tir simultan&eacute; de deux destructeurs pour
			d&eacute;truire une cible. Un destructeur seul ne peut donc rien
			d&eacute;truire ni d&eacute;fendre.</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>Toute destruction
			co&ucirc;te donc deux points : un point par tir.</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2><I><B>D&eacute;finition
			:</B></I> une case est <I>sous le feu adverse</I> lorsqu'elle est
			situ&eacute;e &agrave; port&eacute;e de tir de plusieurs
			destructeurs du m&ecirc;me adversaire.<BR>Une pi&egrave;ce ne peut
			jamais se placer ou se d&eacute;placer sous le feu adverse ; seul
			un destructeur peut venir se placer sous le feu adverse, et
			uniquement en cas d'attaque.</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=3><B>Attaque d'un
			secteur d&eacute;fendu par le feu adverse</B></FONT><FONT SIZE=2><BR>Lorsqu'un
			joueur attaque un tel secteur, ses destructeurs n'arrivent jamais
			en m&ecirc;me temps aupr&egrave;s de l'adversaire (m&ecirc;me si
			les tirs, eux, sont simultan&eacute;s) : il y a toujours un
			premier, puis un second arrivant.<BR>Le premier destructeur
			arrivant ne peut jamais se placer, au moment de son tir, sous le
			feu adverse.<BR>Le deuxi&egrave;me destructeur arrivant, lui, peut
			venir au besoin se placer jusque sous le feu adverse, pour
			aussit&ocirc;t s'arr&ecirc;ter et tirer (simultan&eacute;ment avec
			le premier arrivant). Mais au terme de son (ses) tirs, et avant
			tout autre d&eacute;placement, ce deuxi&egrave;me arrivant doit
			avoir d&eacute;truit assez de d&eacute;fenseurs pour ne plus &ecirc;tre
			sous le feu adverse. Alors seulement, le joueur attaquant peut
			faire d'autres mouvements.<BR>La </FONT><FONT SIZE=2><I>Th&eacute;orie
			du Deuxi&egrave;me Arrivant</I></FONT> <FONT SIZE=2>se r&eacute;sume
			ainsi : un destructeur peut arriver jusque sous le feu adverse si
			ses tirs permettent d'annihiler ce feu adverse, avant tout autre
			mouvement.<BR>Si le deuxi&egrave;me arrivant arrive transport&eacute;
			vers un secteur d&eacute;fendu par le feu adverse, il doit &ecirc;tre
			d&eacute;charg&eacute; sur une case hors-feu, puis se rendre par
			ses propres moyens sur sa case de tir (Toutes les attaques ne sont
			pas forc&eacute;ment appel &agrave; la Th&eacute;orie du Deuxi&egrave;me
			Arrivant.).</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=3><B>Capture</B></FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2><I><B>Principe de la
			capture :</B></I> deux destructeurs d'un m&ecirc;me joueur, au
			contact d'une pi&egrave;ce de couleur diff&eacute;rente, peuvent
			la faire passer &agrave; leur propre couleur.<BR>Le changement de
			couleur se mat&eacute;rialise aussit&ocirc;t par un changement de
			marqueur.<BR>Cette op&eacute;ration co&ucirc;te un point.<BR>Au
			moment de la capture, preneurs et captur&eacute;s doivent &ecirc;tre
			hors du feu adverse (un joueur peut donc prot&eacute;ger une pi&egrave;ce
			de la capture en l'abritant sous son feu).<BR>Une pi&egrave;ce
			captur&eacute;e est imm&eacute;diatement op&eacute;rationnelle
			pour toute action.<BR>M&ecirc;me &agrave; court de munitions, un
			destructeur peut participer &agrave; une capture.<BR>Le contenu
			d'un transporteur captur&eacute; change automatiquement de
			couleur, sans d&eacute;pense de points suppl&eacute;mentaires.<BR>Rappelons
			qu'il n'est pas n&eacute;cessaire de proc&eacute;der &agrave; une
			capture pour s'emparer d'un ponton.<BR>La capture d'un astronef
			est une tout autre paire de manches (voir <I>astronef</I>).</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=3><B>Recul et
			neutralisation</B></FONT><FONT SIZE=2><BR>Au terme d'une
			manoeuvre, des destructeurs peuvent tenir sous leur feu des pi&egrave;ces
			adverses, sans pour autant les d&eacute;truire ou les
			capturer.<BR>Dans ce cas, lorsque arrive son tour, le joueur
			menac&eacute; peut tenter de se mettre hors-feu : il a le droit,
			pour cela, de bouger d'une seule case chaque pi&egrave;ce menac&eacute;e,
			&agrave; condition que la nouvelle case occup&eacute;e soit
			hors-feu. <BR>S'il a d&eacute;cid&eacute; de reculer, il doit le
			faire avant tout autre action. Au hasard, une contre-attaque.<BR>Une
			pi&egrave;ce sous le feu adverse et ne pouvant pas reculer est
			neutralis&eacute;e elle ne peut strictement rien faire. Ce n'est
			qu'avec d'autres destructeurs qu'un joueur pourra d&eacute;neutraliser
			cette pi&egrave;ce.<BR>D&egrave;s qu'il a fait une neutralisation,
			un destructeur ne peut plus bouger jusqu'&agrave; la fin de son
			tour. Lorsque revient son tour, le joueur qui neutralise une pi&egrave;ce
			peut tenter de la d&eacute;truire ou de la prendre.<BR>Une
			tourelle n'est pas neutralisable. M&ecirc;me sous le feu, elle
			peut toujours tirer. </FONT>
			</P>
			<P STYLE="margin-bottom: 0.5cm">&nbsp;</P>
			<HR>
			<P STYLE="margin-bottom: 0.5cm"><IMG SRC="images/regles_html_m6797146f.gif" NAME="Image6" ALIGN=LEFT WIDTH=273 HEIGHT=263 BORDER=0><FONT SIZE=4><B>L'ASTRONEF</B></FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>L'astronef, compos&eacute;
			d'une bulle centrale et de trois podes surmont&eacute;es de
			tourelles, occupe quatre cases sur le plateau. A part ses
			tourelles, l'astronef et sa cargaison sont indestructibles. Il
			constitue le refuge (et le seul point de d&eacute;part) des pi&egrave;ces
			qui s'y trouvent.<BR>Tout &eacute;l&eacute;ment pr&eacute;sent
			dans un astronef doit &ecirc;tre plac&eacute; face &agrave;
			celui-ci, hors du plateau, visible des autres joueurs.<BR>Un
			astronef ne peut occuper que des cases plaine et mar&eacute;cages
			(il ne subit pas l'effet des mar&eacute;es). </FONT>
			</P>
			<P STYLE="margin-bottom: 0.5cm"><BR><BR>
			</P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=3><B>Rentrer et sortir
			de l'astronef</B></FONT><FONT SIZE=2><BR>Les &eacute;l&eacute;ments
			rentrent ou sortent de l'astronef par un de ses trois podes, sur
			toute case adjacente appropri&eacute;e (la bulle centrale n'a pas
			d'acc&egrave;s sur l'ext&eacute;rieur).<BR>Le minerai rentr&eacute;
			dans un astronef ne peut ni en sortir ni &ecirc;tre
			transform&eacute;.<BR>Entrer ou sortir un transporteur charg&eacute;
			ne co&ucirc;te qu'un point.<BR>A l'int&eacute;rieur de l'astronef,
			le chargement et le d&eacute;chargement d'un transporteur ne
			co&ucirc;tent pas de points.<BR>Dans le m&ecirc;me tour, on peut
			rentrer puis sortir une m&ecirc;me pi&egrave;ce par toute case
			disponible bordant l'astronef.<BR>Pour toute entr&eacute;e ou
			sortie d'&eacute;l&eacute;ment, l'astronef est assimil&eacute; &agrave;
			un transporteur et fonctionne selon les m&ecirc;mes r&egrave;gles,
			MAIS :</FONT></P>
			<UL>
				<LI><P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>une pi&egrave;ce
				peut entrer ou sortir par un pode situ&eacute; sous le feu
				adverse. (&agrave; condition d'arriver sur une case hors-feu)</FONT>
								</P>
				<LI><P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>une pi&egrave;ce
				ne peut pas sortir par un pode dont la tourelle est d&eacute;truite.
				</FONT>
				</P>
				<LI><P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>une pi&egrave;ce
				(ou minerai) peut entrer par un pode dont la tourelle est
				d&eacute;truite.</FONT> 
				</P>
			</UL>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=3><B>Prise d'un
			astronef</B></FONT> 
			</P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>Lorsque ses trois
			tourelles sont d&eacute;truites, un astronef et toutes les pi&egrave;ces
			&agrave; sa couleur passent sous le contr&ocirc;le du premier
			joueur qui fait p&eacute;n&eacute;trer un destructeur dans cet
			astronef, lequel conserve sa couleur d'origine.<BR>D&egrave;s
			qu'il a pris le contr&ocirc;le d'un astronef, le joueur b&eacute;n&eacute;ficie
			d'une augmentation de cinq points de son cr&eacute;dit de base,
			utilisables aussit&ocirc;t s'il le d&eacute;sire.<BR>Un joueur
			contr&ocirc;lant plusieurs astronefs continue &agrave; ne jouer
			qu'une fois, &agrave; son tour de jeu. S'il perd un de ses
			astronefs, son cr&eacute;dit de base diminue de cinq points
			(exemple : il en avait trois, il s'en est fait prendre un, son
			cr&eacute;dit de base redescend de 25 &agrave; 20 points).<BR>Tout
			joueur ne poss&eacute;dant plus d'astronef a perdu la partie et
			rentre &agrave; pied. Ses cubes de bonus retournent dans la
			bo&icirc;te.<BR>Seul le joueur qui vient de prendre (ou reprendre)
			un astronef, peut en reconstruire les tourelles en un ou plusieurs
			tours :</FONT></P>
			<UL>
				<LI><P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>pas besoin du
				minerai ni de la pondeuse. </FONT>
				</P>
				<LI><P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>jamais sur un
				pode sous le feu adverse. </FONT>
				</P>
				<LI><P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>reconstruire une
				tourelle co&ucirc;te deux points. </FONT>
				</P>
				<LI><P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>une tourelle
				reconstruite peut tirer imm&eacute;diatement .</FONT> 
				</P>
			</UL>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>Le joueur qui a pris
			un astronef utilise les pi&egrave;ces ainsi contr&ocirc;l&eacute;es
			(dans l'astronef et sur le plateau), conjointement avec ses
			propres pi&egrave;ces. C'est une arm&eacute;e mixte, dont les
			pi&egrave;ces conservent chacunes leurs couleurs (et leurs
			emplacements distincts, hors du plateau, si elles se trouvent dans
			les astronefs).</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2><B>Cas
			particulier</B><BR><I>cons&eacute;quences d'un changement de
			propri&eacute;taire au sein d'une arm&eacute;e mixte, suite &agrave;
			la capture d'un astronef :</I> Dans ce cas, le nouveau
			propri&eacute;taire d'un astronef doit, avant toute action, mettre
			les pi&egrave;ces de sa nouvelle arm&eacute;e hors du feu devenu
			adverse (voir <I>recul et neutralisation</I>. Les pi&egrave;ces
			qui n'ont pas pu se mettre hors-feu sont neutralis&eacute;es par
			l'adversaire.</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2><I><B>Remarques</B></I></FONT></P>
			<UL>
				<LI><P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>M&ecirc;me
				d&eacute;muni de ses trois tourelles, un astronef reste la
				propri&eacute;t&eacute; de son joueur, avec tous ses pouvoirs,
				tant qu'un destructeur adverse n'a pas p&eacute;n&eacute;tr&eacute;
				&agrave; l'int&eacute;rieur.</FONT> 
				</P>
				<LI><P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>La prise d'un
				astronef est l'unique cas o&ugrave; une pi&egrave;ce
				op&eacute;rationnelle peut p&eacute;n&eacute;trer dans un
				astronef adverse.</FONT> 
				</P>
				<LI><P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>Le destructeur
				ne peut &eacute;videmment p&eacute;n&eacute;trer dans cet
				astronef qu'en empruntant un chemin situ&eacute; hors du feu
				adverse...</FONT> 
				</P>
				<LI><P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>... mais il peut
				y entrer par un pode situ&eacute;, lui, sous le feu adverse.</FONT>
								</P>
			</UL>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=3><B>Changement de
			couleur</B></FONT><FONT SIZE=2><BR>Les marqueurs-couleur &eacute;quipant
			chaque pi&egrave;ce sont des r&eacute;cepteurs qui n'ob&eacute;issent
			qu'&agrave; un seul &eacute;metteur : celui de l'astronef de cette
			couleur.<BR>Il n'y a que deux mani&egrave;res de changer la
			couleur d'une pi&egrave;ce :</FONT></P>
			<UL>
				<LI><P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>en proc&eacute;dant
				&agrave; une capture (voir </FONT><FONT SIZE=2><I>capture</I></FONT><FONT SIZE=2>).</FONT>
								</P>
				<LI><P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>toute pi&egrave;ce
				p&eacute;n&eacute;trant dans un astronef prend la couleur de cet
				astronef, et ob&eacute;it au joueur qui le contr&ocirc;le.</FONT>
								</P>
			</UL>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>Un joueur contr&ocirc;lant
			des pi&egrave;ces de couleur diff&eacute;rentes dispose de ces
			deux moyens s'il veut proc&eacute;der &agrave; des changements de
			couleur sur ses propres pi&egrave;ces.<BR>Un astronef conserve
			toujours sa couleur (son &eacute;metteur) d'origine, m&ecirc;me
			s'il est pris par un autre joueur. </FONT>
			</P>
			<P STYLE="margin-bottom: 0.5cm">&nbsp;</P>
			<HR>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=4><B>DEMARRAGE D'UNE
			PARTIE</B></FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=3><B>Mise en place du
			jeu</B></FONT><FONT SIZE=2><BR>Parsemez de minerai tout le
			plateau, &agrave; raison d'un pion toutes les trois cases. Puis
			retirez dujeu tout le minerai sur les cases haute mer.<BR></FONT><FONT SIZE=2><I>La
			r&eacute;serve</I></FONT> <FONT SIZE=2>: Les pi&egrave;ces cr&eacute;&eacute;es
			par les pondeuses seront puis&eacute;es dans la r&eacute;serve
			commune, qui doit contenir en d&eacute;but de partie :</FONT></P>
			<UL>
				<LI><P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>Pour deux ou
				quatre joueurs : seize chars, quatre crabes et quatre pontons.</FONT>
								</P>
				<LI><P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2>Pour trois
				joueurs : douze chars, trois crabes et trois pontons.</FONT> 
				</P>
			</UL>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2><I>Cartes mar&eacute;es
			</I>: Battez les quinze cartes, &eacute;cartez-en six, et placez
			les neuf autres dans l'alv&eacute;ole <I>futures mar&eacute;es</I>.
			Le tout sans regarder aucune carte.</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=3><B>Premiers tours</B></FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2><B>Tour 1</B><BR>Arriv&eacute;e
			des astronefs.<BR>Placez le cube-t&eacute;moin dans le cran n' 1
			du compte-tours, et tirez au sort votre ordre d'arriv&eacute;e sur
			la plan&egrave;te.<BR>Chaque joueur a trois minutes maximum pour
			poser son astronef, marqu&eacute; &agrave; sa couleur, &agrave;
			l'int&eacute;rieur d'une zone d'arriv&eacute;e : une des deux
			&icirc;les, ou une portion de terre comprise entre deux lignes
			pointill&eacute;es.<BR>Un astronef occupe quatre cases, aux
			conditions suivantes : laisser un passage d'au moins une case au
			bord du plateau ; n'occuper que des cases plaines et ou
			mar&eacute;cages.<BR>Deux astronefs ne peuvent pas occuper des
			zones contig&uuml;es (une &icirc;le n'est pas une zone
			contig&uuml;e).<BR>La zone d'arriv&eacute;e d'un joueur (y compris
			les cases r&eacute;cifs touchant la c&ocirc;te) est vid&eacute;e
			de ses pions-minerai, qui sont remis dans la bo&icirc;te.<BR>Vos
			places d&eacute;finitives autour de la table sont d&eacute;termin&eacute;es
			par la disposition de vos astronefs sur le plateau. changez donc
			de chaise si besoin est.<BR>Posez votre arm&eacute;e devant vous,
			hors du plateau : une barge. un crabe, une pondeuse-m&eacute;t&eacute;o,
			deux vedettes, quatre chars, un gros tas et un ponton.<BR>Marquez-l&agrave;
			&agrave; votre couleur.</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2><B>Tour 2</B><BR>D&eacute;ploiement
			(sans d&eacute;pense de points).<BR>Avancez le t&eacute;moin dans
			le deuxi&egrave;me cran du compte-tours. Vous disposez ensemble de
			trois minutes pour d&eacute;ployer tout ou partie de vos pi&egrave;ces
			hors de l'astronef, dans la limite de vos zones d'arriv&eacute;e.<BR>Les
			ponts et pi&egrave;ces marines d&eacute;ploy&eacute;es doivent
			toucher la c&ocirc;te de leur zone. Les transporteurs peuvent &ecirc;tre
			d&eacute;plov&eacute;s charq&eacute;s.<BR>Pour ce tour, on
			consid&egrave;re que la mar&eacute;e est normale, mais attention &agrave;
			la suivante!</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2><B>Tour 3</B><BR>Cinq
			points.<BR>Avancez le t&eacute;moin au cran n'3.<BR>Tirage au sort
			d&eacute;finitif du premier joueur. Les autres suivront dans le
			sens des aiguilles d'une montre.<BR>renez connaissance de la
			premi&egrave;re carte-mar&eacute;e : placez-la. visible. dans
			l'alv&eacute;ole <I>mar&eacute;e pr&eacute;sente</I>. Elle indique
			la mar&eacute;e de ce troisi&egrave;me tour.<BR>Les joueurs dont
			la pondeuse-m&eacute;t&eacute;o est op&eacute;rationnelle (hors
			astronef. hors transporteur et non embourb&eacute;e), regardent la
			seconde carte, qui indique la mar&eacute;e du prochain tour. Si
			chaque joueur a une pondeuse m&eacute;t&eacute;o op&eacute;rationnelle,
			cette carte est simplement retourn&eacute;e, visible. sur le
			dessus de la pile &agrave; futures mar&eacute;es. Il en sera ainsi
			au d&eacute;but de chaque tour de jeu.<BR>Vous disposez, l'un
			apr&egrave;s l'autre, de cinq points et trois minutes pour vos
			actions.</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2><B>Tour 4</B><BR>D&eacute;pense
			maximum : dix points.<BR>La carte <I>future mar&eacute;e</I>
			change de pile et devient <I>mar&eacute;e pr&eacute;sente</I>
			visible de tous les joueurs.<BR>Les possesseurs de pondeuse-m&eacute;t&eacute;o
			op&eacute;rationnelle prennent connaissance de la prochaine mar&eacute;e,
			sur la pile de gauche.<BR>Chaque joueur peut jouer dix points.</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2><B>Tour 5</B><BR>Avez-vous
			pens&eacute; au compte-tours ?<BR>Chaque joueur peut jouer
			d&eacute;sormais quinze points.<BR>La carte <I>future mar&eacute;e</I>
			change de pile, la prochaine mar&eacute;e est regard&eacute;e par
			ceux qui le peuvent, et ainsi de suite jusqu'au 25&egrave;me tour
			(au d&eacute;but du 11&egrave;me et 19&egrave;me tour, quand la
			pile <I>futures mar&eacute;es</I> sera &eacute;puis&eacute;e ;
			vous rebattrez alors toutes les cartes - sauf la <I>mar&eacute;e
			pr&eacute;sente</I> -, et en &eacute;carterez six avant de
			regarder la future mar&eacute;e, etc.).</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2><B>Tour
			6</B><BR>Compte-tours. mar&eacute;es, quinze points, la partie
			adopte sa vitesse de croisi&egrave;re, vous devenez un vrai Full
			Metal Pilote.<BR>M&eacute;fiez-vous quand m&ecirc;me de ceux qui
			ont d&eacute;j&agrave; &eacute;conomis&eacute; des points.</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><IMG SRC="images/regles_html_m20106131.gif" NAME="Image5" ALIGN=LEFT WIDTH=304 HEIGHT=256 BORDER=0><FONT SIZE=3><B>Fin
			d'une partie</B></FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2><B>Tour 21</B><BR>Le
			d&eacute;part au 21&egrave;me tour se fait de la mani&egrave;re
			suivante : au d&eacute;but du 21&egrave;me tour, secr&egrave;tement,
			chaque joueur met dans sa main ferm&eacute;e une pi&egrave;ce ou
			un minerai. La pi&egrave;ce signifie que l'astronef restera sur la
			plan&egrave;te jusqu'au 25&egrave;me tour ; le minerai signifie
			que l'astronef partira imm&eacute;diatement. Chaque joueur avance
			la main au milieu du plateau, &agrave; raison d'une main par
			astronef qu'il contr&ocirc;le, puis tous les joueurs ouvrent la
			(les) main(s) en m&ecirc;me temps.<BR>Ceux qui ont d&eacute;cid&eacute;
			de partir le font imm&eacute;diatement, sans jouer leur tour, avec
			ce que leur(s) astronefs) contenait &agrave; la fin du vingti&egrave;me
			tour. Un joueur poss&eacute;dant plusieurs astronefs peut prendre
			des d&eacute;cisions distinctes pour chacun d'eux. Mais faire
			partir au 21, tour un astronef sur deux (par exemple), ram&egrave;ne
			son cr&eacute;dit de base de vingt &agrave; quinze points pour les
			tours suivants.<BR>Les joueurs ayant fait partir des astronefs en
			comptabilisent les points gagnants : deux points par minerai, un
			point par pi&egrave;ce contenue dans l'astronef (chaque tourelle
			vaut un point).<BR>Les pi&egrave;ces abandonn&eacute;es par les
			astronefs de leur couleur sont neutralis&eacute;es : les joueurs
			restants peuvent venir les d&eacute;truire ou les capturer.</FONT></P>
			<P STYLE="margin-bottom: 0.5cm"><BR><BR>
			</P>
			<P STYLE="margin-bottom: 0.5cm"><FONT SIZE=2><B>Tour 25</B><BR>Si
			un ou plusieurs joueurs ont d&eacute;cid&eacute; de rester, ils
			jouent le 21&egrave;me tour et les suivants. Ils font partir leurs
			astronefs au 25&egrave;me tour, apr&egrave;s avoir jou&eacute;
			leur tour.<BR><B>Attention</B> : en jouant le 25&egrave;me tour,
			il faut pr&eacute;voir un point pour le d&eacute;collage d'un
			astronef intact, et un point de plus par tourelle manquante.<BR>Apr&egrave;s
			le 25&egrave;me tour, les joueurs comptent leurs points gagnants
			comme au 21&egrave;me tour. Le joueur totalisant le plus grand
			nombre de points a gagn&eacute;. En cas d'ex-aequo, c'est le
			joueur poss&eacute;dant le plus grand nombre d'astronefs qui a
			gagn&eacute;. En cas de nouvelle &eacute;galit&eacute;, c'est le
			joueur qui ram&egrave;ne la plus grande quantit&eacute; de minerai
			qui a gagn&eacute;.<BR><I>Rappel</I> : tout point d&eacute;pens&eacute;
			est comptabilis&eacute; d&eacute;finitivement. Si, au terme des
			points d&eacute;pens&eacute;s, un joueur ne peut plus faire
			d&eacute;coller son astronef, celui-ci reste sur Full Metal
			Plan&egrave;te o&ugrave; il rouillera jusqu'&agrave; la fin des
			temps.</FONT></P>
			<P><BR>
			</P>

<%@include file="/include/footer.jsp"%>
</body>
</HTML>