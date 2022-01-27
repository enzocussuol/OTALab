let gifCarregando = document.querySelector(".gifCarregando");
let caixaDispositivos = document.querySelector(".caixaDispositivos");

async function atualizaDispositivos(){
	caixaDispositivos.style.display = "none";
	gifCarregando.style.display = "initial";
	
	let response = await fetch("/atualizaDispositivos", {
		method: "POST"
	});
	
	if(response.status == 200){
		document.location.reload(true);
		alert("Dispositivos atualizados");
	}else{
		alert("Ocorreu um erro ao tentar atualizar os dispositivos vivos");
	}
}

async function uploadFile(element){
	caixaDispositivos.style.display = "none";
	gifCarregando.style.display = "initial";
	
	let fileUpload = document.getElementById("fileUpload");
	let idDispositivo = element.parentElement.querySelector("#idDispositivo");
	let nomeDispositivo = element.parentElement.querySelector("#nomeDispositivo");
	
	let formData = new FormData();
	
	formData.append("file", fileUpload.files[0]);
	formData.append("idDispositivo", idDispositivo.innerHTML);
	formData.append("nomeDispositivo", nomeDispositivo.innerHTML);
	
	let response = await fetch("/upload", {
		method: "POST",
		body: formData
	});
	
	if(response.status == 200){
		alert("Código enviado com sucesso");
	}else{
		alert("Ocorreu um erro ao tentar enviar o código");
	}
	
	gifCarregando.style.display = "none";
	caixaDispositivos.style.display = "initial";
}