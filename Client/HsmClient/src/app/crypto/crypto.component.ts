import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { ApiService } from '../api.service';
import { FormGroup, SelectControlValueAccessor } from '@angular/forms';
import { FormBuilder } from '@angular/forms';
import { ThrowStmt } from '@angular/compiler';

interface eKeys {
  value: string;
  viewValue: string;
}

@Component({
  selector: 'app-crypto',
  templateUrl: './crypto.component.html',
  styleUrls: ['./crypto.component.css']
})
export class CryptoComponent implements OnInit {

    displayKeys: eKeys[] = [
      {value: '', viewValue: ''},
    ];
    keyRing: FormGroup;
  keyForm: FormGroup;
  encryptForm: FormGroup;
  decryptForm: FormGroup;
  hashForm: FormGroup;
  signForm: FormGroup;

  currentUserIDKeys;
  currentlySelectedKey;
  cipherText;


  decData;
  text; // Testing text for ensuring calls work REMOVE ME
  publicKey = 'Key goes here';
  eKeyID = '';
  keyPass = '';
  publicKeyRing = '';

  constructor(private http: HttpClient,
              private formBuilder: FormBuilder,
              private apiService: ApiService) { }

  ngOnInit(): void {
    this.keyForm = this.formBuilder.group({
      keyPassword: [''],
    });
    this.encryptForm = this.formBuilder.group({
      textToEncrypt: [''],
    });
    this.decryptForm = this.formBuilder.group({
      textToDecrypt: [''],
    });
    this.hashForm = this.formBuilder.group({
      textToHash: [''],
    });
    this.signForm = this.formBuilder.group({
      textToSign: [''],
    });
    this.keyRing = this.formBuilder.group({
      displayKeys: [''],
    });

    this.populateKeyIDLOV();
  }

  public populateKeyIDLOV() {
    this.currentUserIDKeys = this.apiService.currentUserKeyIDs;
    console.log(this.currentUserIDKeys);
//  this.displayKeys.push({value: Object.keys(res)[0], viewValue: Object.keys(res)[0]});
  }

  /**
   * Generate a pair of private and public keys using RSA.
   *
   * A key pair is generated using RSA, a key id is used to link this key to the user id.
   * Private key is stored AES256 encrypted in the HSM DB. Key encryption key is
   * calculated as follows: KEK = HSM Secret Key XOR SHA256(Key Password).
   *
   * @argument Key Password.
   * @returns Key ID, Public Key.
   */
  public onGenerateKeys() {
    const url = 'http://localhost:8080/generateKeyPair';
    this.keyPass = this.keyForm.get('keyPassword').value;
    this.http.get(url,
      {
        params: new HttpParams().set('keyPassword', this.keyForm.get('keyPassword').value)
          .append('userID', this.apiService.getRegisteredUser())
      },
    ).subscribe(
      (res: Response) => {
        if (Object.values(res)[0] + '' === '500') {
          console.log('Key failed to generate');
        } else {
          this.publicKeyRing = Object.keys(res)[0];
          this.publicKey = Object.values(res)[0]; // TODO Store on db.
          this.eKeyID = Object.keys(res)[0];


          this._updateData(this.publicKey);
          if(this.publicKeyRing !== 'False') {
            this._updateFunction('Public Key:');
            this.displayKeys.push({value: Object.keys(res)[0], viewValue: Object.keys(res)[0]});
         //   this.keyRing.patchValue({value: 'key-1', viewValue: Object.keys(res)[0]});
          } else {
            this._updateFunction('Warning:');
          }
          this._updateDisplayedData('Affirmative');
        }
      },
    );
  }

  // public getPublicKeys() {
  //   const url = 'http://localhost:8080/getPublicKeys';

  //   this.http.get(url,
  //     {
  //       params: new HttpParams().set('keyPassword', this.keyForm.get('keyPassword').value)
  //         .append('userID', this.apiService.getRegisteredUser())
  //     },
  //   ).subscribe(
  //     (res: Response) => {
  //       if (Object.values(res)[0] + '' === '500') {
  //         console.log('Key failed to generate');
  //       } else {
  //         this.publicKeyRing = Object.keys(res)[0];
  //         this.publicKey = Object.values(res)[0]; // TODO Store on db.
  //         this.eKeyID = Object.keys(res)[0];

  //         this._updateData(this.publicKey);
  //         this._updateFunction('Public Key:');
  //         this._updateDisplayedData('yte');
  //       }
  //     },
  //   );
  // }

  /**
   *  Shows the public key for the user.
   */
  private _updateData(data: string) {
    this.apiService.updatedDataSelection(data);
  }

  private _updateFunction(data: string) {
    this.apiService.updateLastFunctionCalled(data);
  }

  private _updateDisplayedData(data: string) {
    this.apiService.updateDisplayedData(data);
  }

  /**
   * Locates the private key corresponding to the provided Key ID. Returns the
   * encryption of the provided text.
   * @argument Text, Key ID, Key Password
   * @returns RSA(Text, Private Key from HSM DB)
   */
  public onEncrypt() {
    const url = 'http://localhost:8080/encrypt';
    this.http.get(url,
      {
        params: new HttpParams().set('text', this.encryptForm.get('textToEncrypt').value)
          .append('eKeyID', this.currentlySelectedKey)
          .append('keyPassword', this.keyForm.get('keyPassword').value)
      },
    ).subscribe(
      (res: Response) => {
        console.log(res);
        // this.publicKey = Object.values(res)[0];
        // this.eKeyID = Object.keys(res)[0];
        this.cipherText = Object.values(res)[0];
        this._updateData(this.cipherText);
        this._updateFunction('Encrypted Text:');
        this._updateDisplayedData('Affirmative');
      },
    );
  }

  public onDropdownSelection(selected: SelectControlValueAccessor) {
    this.currentlySelectedKey = selected.value;
    const url = 'http://localhost:8080/getPubKeys';
    this.http.get(url,
      {
        params: new HttpParams().set('eKeyID', selected.value)
      },
    ).subscribe(
      (res: Response) => {
        this._updateData(Object.values(res)[0]);
        this._updateFunction('Key ID: ' + this.currentlySelectedKey + ' | Public Key:');
        this._updateDisplayedData('Affirmative');
      },
    );
  }

  /**
   *  FIll me in
   */
  public onDecrypt() {
    const cipherText = this.apiService.getData();
    const url = 'http://localhost:8080/decrypt';
    this.http.get(url,
      {
        params: new HttpParams().set('keyPassword', this.keyPass)
          .append('cipherText', this.decryptForm.get('textToDecrypt').value)
          .append('eKeyID', this.currentlySelectedKey)
      },
    ).subscribe(
      (res: Response) => {
        console.log(res);
        this._updateData(Object.values(res)[0]);
        this._updateFunction('Decrypted Text:');
        this._updateDisplayedData('Affirmative');
      },
    );
  }

  /**
   * Performs SHA-256 hashing on a given text.
   * @argument String that does X
   * @returns String
   */
  public onHash() {
    const url = 'http://localhost:8080/hash';
    this.http.get(url,
      {
        params: new HttpParams().set('textToHash', this.hashForm.get('textToHash').value)
      },
    ).subscribe(
      (res) => {
        this._updateData(Object.values(res)[0]);
        this._updateFunction('Hash:');
        this._updateDisplayedData('Affirmative');
      },
    );
  }

  /**
   * Creates a digital signature from an established symmetric key.
   */
  public onSign() {
    const url = 'http://localhost:8080/sign';
    this.http.get(url,
      {
        params: new HttpParams().set('textToSign', this.signForm.get('textToSign').value)
          .append('eKeyID', this.eKeyID)
          .append('keyPassword',  this.keyForm.get('keyPassword').value)
      },
    ).subscribe(
      (res: Response) => {
        this._updateData(Object.values(res)[0]);
        this._updateFunction('Signature:');
        this._updateDisplayedData('Affirmative');
      },
    );
  }

  /**
   * Verifies the information from a digital signature.
   */
  public onVerify() {
    const testText = 'Encrypt Me';
    const testKeyPassword = 'myPassword';
    const url = 'http://localhost:8080/verify';
    this.http.get(url,
      {
        params: new HttpParams().set('text', testText)
          .append('eKeyID', this.eKeyID)
          .append('keyPassword', testKeyPassword)
      },
    ).subscribe(
      (res: Response) => {
        console.log(res);
      },
    );
  }

  /**
   * Generates a report summarizing the status of the HSM. A list of the registered
   * users and stored keys.
   */
  public onGenReport() {
    const url = 'http://localhost:8080/generateReport';
    this.http.get(url,
      //  {
      //  params: new HttpParams().set('keyPassword', this.keyForm.get('keyPassword').value)
      //  },
    ).subscribe(
      (res) => {
        let print = '';
        for (const [key, value] of Object.entries(res)) {
          print += (`${key}: ${value}` + '  ');
        }
        console.log(res);
        this.publicKey = print;

        this._updateData(print);
        this._updateFunction('Report:');
        this._updateDisplayedData('Affirmative');
      },
    );
  }

  public onShiftText() {
    this.decData = this.cipherText;
  }
}
