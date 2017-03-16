package labs.mobile.lab_6_piratecrew;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    static final int SELECT_PIRATE = 1;
    static final int SELECT_SHIP = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickDownloadData(View pView){
        Intent intent = new Intent(this, DisplayJSONActivity.class);
        startActivity(intent);
    }

    public void onClickEditPirate(View pView){
        Intent intent = new Intent(this, SelectPirateActivity.class);
        startActivityForResult(intent, SELECT_PIRATE);
    }

    public void onClickAddPirate(View pView){
        Intent intent = new Intent(this, EditPirateActivity.class);
        startActivity(intent);
    }

    public void onClickEditShip(View pView){
        Intent intent = new Intent(this, SelectShipActivity.class);
        startActivityForResult(intent, SELECT_SHIP);
    }

    public void onClickAddShip(View pView){
        Intent intent = new Intent(this, EditShipActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int pRequestCode, int pResultCode, Intent pData){
        if(pResultCode == RESULT_OK){
            if(pRequestCode == SELECT_PIRATE) {
            }
            else if(pRequestCode == SELECT_SHIP) {
            }
        }

    }
}
