let gifCarregando = document.querySelector(".gifCarregando");
let caixaDispositivos = document.querySelector(".caixaDispositivos");

function exibeGifCarregando(){
	caixaDispositivos.style.display = "none";
	gifCarregando.style.display = "initial";
}

async function atualizaDispositivos(){
	exibeGifCarregando();
	
	let response = await fetch("/handleAtualizaDispositivos", {
		method: "POST"
	});
	
	document.location.reload(true);

	if(response.status == 200){
		alert("Dispositivos atualizados");
	}else{
		alert("Ocorreu um erro ao tentar atualizar os dispositivos vivos");
	}
}

async function uploadFile(element){
	let fileUpload = document.getElementById("fileUpload");
	
	if(fileUpload.value === ""){
		alert("Impossível realizar upload sem selecionar um arquivo");
		return;
	}
	
	exibeGifCarregando();
	
	let idDispositivo = element.parentElement.children[1];
	let nomeDispositivo = element.parentElement.parentElement.children[0].children[0];
	
	let formData = new FormData();
	
	formData.append("file", fileUpload.files[0]);
	formData.append("idDispositivo", idDispositivo.innerHTML);
	formData.append("nomeDispositivo", nomeDispositivo.innerHTML);
	
	let response = await fetch("/handleFileUpload", {
		method: "POST",
		body: formData
	});
	
	if(response.status == 200){
		document.location.reload(true);
		alert("Código enviado com sucesso");
	}else{
		alert("Ocorreu um erro ao tentar enviar o código");
	}
}