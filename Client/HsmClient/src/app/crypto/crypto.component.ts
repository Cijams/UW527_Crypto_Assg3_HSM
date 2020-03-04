import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { ApiService } from '../api.service';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { FormGroup } from '@angular/forms';
import { FormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-crypto',
  templateUrl: './crypto.component.html',
  styleUrls: ['./crypto.component.css']
})
export class CryptoComponent implements OnInit {
  keyForm: FormGroup;
  text; // Testing text for ensuring calls work REMOVE ME
  publicKey = 'Key goes here';
  eKeyID = '';

  constructor(private http: HttpClient,
              private formBuilder: FormBuilder,
              private apiService: ApiService) { }

  ngOnInit(): void {
    this.keyForm = this.formBuilder.group({
      keyPassword: [''],
    });
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

          this.publicKey = Object.values(res)[0];
          this.eKeyID = Object.keys(res)[0];
          console.log(this.publicKey);
          console.log(this.eKeyID);
          console.log(res);

          this._updateData(this.publicKey);
          this._updateFunction('Public Key:');
          this._updateDisplayedData('yte');
        }
      },
    );
  }

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
    const testText = 'Encrypt Me';

    const url = 'http://localhost:8080/encrypt';
    this.http.get(url,
      {
        params: new HttpParams().set('text', testText)
          .append('eKeyID', this.eKeyID)
          .append('keyPassword', this.keyForm.get('keyPassword').value)
      },
    ).subscribe(
      (res: Response) => {
        console.log(res);
        // this.publicKey = Object.values(res)[0];
        // this.eKeyID = Object.keys(res)[0];

        this._updateData(Object.values(res)[0]);
        this._updateFunction('Encrypted Text:');
        this._updateDisplayedData('Affirmative');
      },
    );
  }


  /**
   * Decrypts given textfield with inverse of previously used encryption.
   */
  public onDecrypt() {
    const url = 'http://localhost:8080/decrypt';
    this.http.get(url,
      { responseType: 'text' }).subscribe(
        res => {
          this.text = res;
          console.log(res);
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
      { responseType: 'text' }).subscribe(
        res => {
          this.text = res;
          console.log(res);
        },
      );
  }

  /**
   * Creates a digital signature from an established symmetric key.
   */
  public onSign() {
    const testText = 'Encrypt Me';
    const testKeyPassword = 'myPassword';
    const url = 'http://localhost:8080/sign';
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
      {
        params: new HttpParams().set('keyPassword', this.keyForm.get('keyPassword').value)
      },
    ).subscribe(
      (res) => {
        let print = '';
        for (const [key, value] of Object.entries(res)) {
          print += (`${key}: ${value}` + ' | ');
        }
        console.log(res);
        this.publicKey = print;
      },
    );
  }
}
