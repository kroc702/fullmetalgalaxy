<!DOCTYPE html>
<HTML>
<head>
<title>Aide de Full Metal Galaxy</title>
        
<%@include file="/include/meta.jsp"%>

</head>
<body >

<%@include file="/include/header.jsp"%>

<div class="inline-ul" >
<jsp:include page="<%= I18n.localize(request,response,\"/help/menu.html\") %>" />
</div>

<h3>L'astronef</h3>
<table>
  <tr>
    <td><img src="images/freighter.jpg" /></td>
    <td>
      - Base de Commandement, s'il est
        captur&eacute;, toute l'arm&eacute;e passe sous le contrôle du
        conqu&eacute;rant..<br>
        - Pi&egrave;ce qui occupe 4 cases : une bulle centrale et 3
        podes munis de tourelles.<br>

        - Ne se d&eacute;place pas ; doit être pos&eacute; sur des cases de
        terre ferme ou de marais.<br>
        - Les tourelles sont des destructeurs d'une port&eacute;e de 2
        cases.<br>
        - Peut charger des pi&egrave;ces par ses podes.<br>
        - Peut d&eacute;charger des pi&egrave;ces (sauf le minerai) par ses
        podes &agrave; tourelle intacte.
    </td>
  </tr>
</table>

<h3>Les transporteurs</h3>
<p> Ils peuvent être utilisés pour récolter le minerais ou pour transporter 
vos destructeurs à des fins plus agressives :</p>

<table>
  <tr>
    <td><img src="images/weatherhen.png" /></td>
    <td>Pondeuse meteo</td>
    <td>
      - Engin terrestre.<br>
        - Pr&eacute;voit les mar&eacute;es &agrave; venir lorqu'elle est sortie et
        non neutralis&eacute;e.<br>
        - Peut transporter un minerai.<br>
        - Peut convertir un minerai en une pi&egrave;ce de la reserve
        (Char, Crabe ou Ponton).<br>
        - Encombrement de deux places.
    </td>
  </tr>
  <tr>
    <td><img src="images/crab.png" /></td>
    <td>Crabe</td>
    <td>
      - Engin terrestre.<br>
        - Transporteur avec une capacit&eacute; de 2 places.<br>
        - Peut transporter les Minerais, les Chars, les Gros Tas
        et les Pontons.<br>
        - Encombrement de deux places.
    </td>
  </tr>
  <tr>
    <td><img src="images/barge.png" /></td>
    <td>Barge</td>
    <td>
      - Engin marin.<br>
        - Pi&egrave;ce qui occupe deux cases.<br>
        - Transporteur marin avec une capacit&eacute; de 4 places.<br>

        - Peut transporter les Minerais, les Chars, les Gros Tas,
        les Pontons, les Crabes et les Pondeuses.<br>
        - Non transportable.
    </td>
  </tr>
</table>

<h3>Les destructeurs</h3>
<table>
  <tr>
    <td><img src="images/tank.png" /></td>
    <td>Char</td>
    <td>
      - Engin terrestre.<br>
        - Destructeur avec une port&eacute;e de 2 cases, 3 depuis les
        montagnes.<br>
        - Encombrement d'une place.
    </td>
  </tr>
  <tr>
    <td><img src="images/heap.png" /></td>
    <td>Gros tas</td>
    <td>
      - Engin terrestre.<br>
        - Destructeur avec une port&eacute;e de 3 cases.<br>
        - Ne peut pas entrer sur une case montagne, même
        lorsqu'il est transport&eacute; par un Crabe.<br>
        - Encombrement d'une place.
    </td>
  </tr>
  <tr>
    <td><img src="images/speedboat.png" /></td>
    <td>Vedette</td>
    <td>
      - Engin marin.<br>
        - Destructeur avec une port&eacute;e de 2 cases.<br>
        - Non transportable.
    </td>
  </tr>
</table>

<h3>Autre</h3>
<table>
  <tr>
    <td><img src="images/pontoon.png" /></td>
    <td>Ponton</td>
    <td>
      - Sans propri&eacute;taire.<br>
        - Sert de pont aux engins terrestres pour aller sur les
        cases innond&eacute;es.<br>
        - Encombrement d'une place.
    </td>
  </tr>
</table>

<p>
D’autre seront sans doute disponible au travers des variantes
</p>

<%@include file="/include/footer.jsp"%>
</body>
</HTML>
