let fileUpload = document.getElementById("fileUpload");
let idDispositivo = document.getElementById("idDispositivo");
let nomeDispositivo = document.getElementById("nomeDispositivo");

async function uploadFile(){
	let formData = new FormData();
	formData.append("file", fileUpload.files[0]);
	formData.append("idDispositivo", idDispositivo.innerHTML);
	formData.append("nomeDispositivo", nomeDispositivo.innerHTML);
	let response = await fetch("/upload", {
		method: "POST",
		body: formData
	});
	
	if(response.status == 200){
		alert("File sucessfully uploaded");
	}
}

async function atualizaDispositivos(){
	let response = await fetch("/atualizaDispositivos", {
		method: "POST"
	});
	
	if(response.status == 200){
		document.location.reload(true);
	}
}