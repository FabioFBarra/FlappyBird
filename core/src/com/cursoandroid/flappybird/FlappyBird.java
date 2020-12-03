package com.cursoandroid.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch;
	private Texture[] passaro;
	private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;
    private Texture gameOver;
    private BitmapFont fonte;
    private BitmapFont mensagem;
    private Circle passaroCirculo;
    private Rectangle retanguloCanoTopo;
    private Rectangle retanguloCanoBaixo;
    private ShapeRenderer shape;

	//Configuração
	private int movimento = 0;
	private float larguraDispositivo;
	private float alturaDispositivo;
    private float variacao = 0;
    private float velocidadeQueda = 0;
    private float posicaoInicialVertical;
    private float posicaoMovimentoCanoHorizontal;
    private float espacoEntreCanos = 300;
    private float deltaTime;
    private Random numeroRandomico;
    private float alturaEntreCanosRandomica;
    private int estadoJogo = 0; //0 = jogo nao iniciado e 1 = jogo rodando e 2 = GAme Over
    private int pontuacao = 0;
    private boolean marcouPonto = false;

    private OrthographicCamera camera;
    private Viewport viewPort;

    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;


	
	@Override
	public void create () {
		batch = new SpriteBatch();
        numeroRandomico = new Random();
        passaro = new Texture[3];

		passaro[0] = new Texture("passaro1.png");
        passaro[1] = new Texture("passaro2.png");
        passaro[2] = new Texture("passaro3.png");
		fundo = new Texture("fundo.png");
        canoBaixo = new Texture("cano_baixo.png");
        gameOver = new Texture("game_over.png");
        canoTopo = new Texture("cano_topo.png");

        fonte = new BitmapFont();
        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(6);

        mensagem = new BitmapFont();
        mensagem.setColor(Color.WHITE);
        mensagem.getData().setScale(3);

		larguraDispositivo = VIRTUAL_WIDTH;
		alturaDispositivo = VIRTUAL_HEIGHT;
        posicaoInicialVertical = alturaDispositivo/2;
        posicaoMovimentoCanoHorizontal = larguraDispositivo;

        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 0);
        viewPort = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

	}

	@Override
	public void render () {

        camera.update();
        //Limpar frames anteriores

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


        deltaTime = Gdx.graphics.getDeltaTime();
        variacao += deltaTime * 10;
        if(variacao>3){
            variacao = 0;
        }

        if (estadoJogo == 0){
            if (Gdx.input.justTouched()){
                estadoJogo = 1;
            }
        } else {


            velocidadeQueda++;
            if(posicaoInicialVertical>0 || velocidadeQueda<0){
                posicaoInicialVertical -= velocidadeQueda;
            }

            if( estadoJogo == 1){

                posicaoMovimentoCanoHorizontal -= deltaTime * 200;
                if(Gdx.input.justTouched()){
                    velocidadeQueda = -10;
                }

                //Verifica se o jogo saiu inteiramente da tela
                if(posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()){
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                    alturaEntreCanosRandomica = numeroRandomico.nextInt(400) - 200;
                    marcouPonto = false;
                }

                //Verifica pontuação
                if(posicaoMovimentoCanoHorizontal < 120){
                    if(!marcouPonto){
                        pontuacao=++pontuacao;
                        marcouPonto = true;
                    }
                }

            } else {
                //Tela de Game Over

                if(posicaoInicialVertical <= 0){
                    if(Gdx.input.justTouched()){
                        estadoJogo = 0;
                        pontuacao = 0;
                        marcouPonto = false;
                        velocidadeQueda = 0;
                        posicaoInicialVertical = alturaDispositivo / 2;
                        posicaoMovimentoCanoHorizontal = larguraDispositivo;
                    }
                }
            }
        }

        //Inserção de imagens (a ordem afeta a sobreposição)
        batch.setProjectionMatrix(camera.combined);


		batch.begin();
		batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, alturaDispositivo/2 + espacoEntreCanos/2 + alturaEntreCanosRandomica);
        batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaDispositivo/2 - canoBaixo.getHeight() - espacoEntreCanos/2 + alturaEntreCanosRandomica);

		batch.draw(passaro[ (int) variacao], 30, posicaoInicialVertical);

        fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo/2, alturaDispositivo-50);

        if(estadoJogo == 2){
            batch.draw(gameOver, larguraDispositivo/2 - gameOver.getWidth()/2, alturaDispositivo/2);
            mensagem.draw(batch, "Toque para Reiniciar", larguraDispositivo/2 - 200, alturaDispositivo/2);
        }

		batch.end();


        passaroCirculo = new Circle();
        passaroCirculo.set(30 + passaro[0].getWidth()/2, posicaoInicialVertical + passaro[0].getHeight()/2, passaro[0].getWidth()/2);
        retanguloCanoBaixo = new Rectangle( posicaoMovimentoCanoHorizontal, alturaDispositivo/2 - canoBaixo.getHeight() - espacoEntreCanos/2 + alturaEntreCanosRandomica, canoBaixo.getWidth(), canoBaixo.getHeight() );
        retanguloCanoTopo = new Rectangle(posicaoMovimentoCanoHorizontal, alturaDispositivo/2 + espacoEntreCanos/2 + alturaEntreCanosRandomica, canoTopo.getWidth(), canoTopo.getHeight());
        //Desenhar Formas

//        shape.begin(ShapeRenderer.ShapeType.Filled);
//        shape.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
//        shape.rect(retanguloCanoBaixo.x, retanguloCanoBaixo.y, retanguloCanoBaixo.width, retanguloCanoBaixo.height);
//        shape.rect(retanguloCanoTopo.x, retanguloCanoTopo.y, retanguloCanoTopo.width, retanguloCanoTopo.height);
//        shape.setColor(Color.RED);
//        shape.end();

        //Teste de colisao

        if(Intersector.overlaps(passaroCirculo, retanguloCanoBaixo) || Intersector.overlaps(passaroCirculo, retanguloCanoTopo) || posicaoInicialVertical <= 0 || posicaoInicialVertical>=alturaDispositivo){
            estadoJogo = 2;

        }


	}

	@Override
    public void resize(int width, int height){
        viewPort.update(width, height);
    }
	

}
