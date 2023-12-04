package bridge.controller;

import bridge.Bridge;
import bridge.BridgeGame;
import bridge.BridgeGameDto;
import bridge.observer.Observer;
import bridge.service.InputService;
import bridge.view.InputView;
import bridge.view.OutputView;

import java.util.List;

public class BridgeController implements Observer {
    private final InputService inputService = new InputService();
    private Bridge bridge;
    private BridgeGame bridgeGame;

    public void run() {
        OutputView.printWelcomeMessage();
        bridge = initGame();
        playGame();
    }

    private Bridge initGame() {
        return inputService.getBridgeSize(InputView::readBridgeSize, OutputView::printError);
    }

    private void playGame() {
        bridge.plusOneTryCount();
        bridgeGame = new BridgeGame(bridge.toDto().bridge());
        bridgeGame.addObserver(this);
        bridgeGame = inputService.getMoving(InputView::readMoving, OutputView::printError, bridgeGame);
        checkGameEnd();
    }

    private void retryGame() {
        inputService.retry(InputView::readGameCommand, this::playGame, OutputView::printError);
    }

    private void checkGameEnd() {
        if (bridgeGame.getCrossResult().size() == bridgeGame.getBridge().size() && !bridgeGame.getCrossResult().contains("X")) {
            bridge.answerCorrect();
            gameResult();
            return;
        }
        else if (bridgeGame.getCrossResult().size() != bridgeGame.getBridge().size() || bridgeGame.getCrossResult().contains("X")) {
            retryGame();
        }
        gameResult();
    }

    private void gameResult() {
        OutputView.printResult(bridgeGame.toDto(), bridge.toDto());
    }

    @Override
    public void display(BridgeGameDto dto) {
        OutputView.printMap(dto);
    }
}
