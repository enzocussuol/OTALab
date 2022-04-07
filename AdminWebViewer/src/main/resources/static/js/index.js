let gifCarregando = document.querySelector(".gifCarregando");
let caixaCadastro = document.querySelector(".caixaCadastro");

async function reqNovaConfiguracaoRede(){	
	let formConfiguracaoRede = document.querySelector(".formConfiguracaoRede");
	
	let respostaRequisicao = await fetch("/handleConfiguracaoRede", {
		method: "POST",
		body: new FormData(formConfiguracaoRede)
	});
	
	document.location.reload(true);
	
	if(respostaRequisicao.status == 200){
		alert("Configuração geral da rede atualizada com sucesso");
	}else{
		alert("Ocorreu um erro ao atualizar a configuração geral da rede");
	}
}

async function reqCadastroDispositivo(){
	caixaCadastro.style.display = "none";
	gifCarregando.style.display = "initial";
	
	let formCadastro = document.querySelector(".formCadastro");
		
	let respostaRequisicao = await fetch("/handleRegistro", {
		method: "POST",
		body: new FormData(formCadastro)
	});
		
	document.location.reload(true);
	
	if(respostaRequisicao.status == 200){
		alert("Dispositivo cadastrado com sucesso");
	}else{
		alert("Ocorreu um erro ao cadastrar o dispositivo");
	}
	
	gifCarregando.style.display = "none";
	caixaCadastro.style.display = "initial";
}