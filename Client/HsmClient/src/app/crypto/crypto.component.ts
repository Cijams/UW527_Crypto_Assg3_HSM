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

  constructor(private http: HttpClient,
              private formBuilder: FormBuilder) { }

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
      },
    ).subscribe(
      (res: Response) => {
        if (Object.values(res)[0] + '' === '500') {
          console.log('Key failed to generate');
        } else {
          this.publicKey = Object.values(res)[0];
        }
      },
    );
  }



  /**
   *  Shows the public key for the user.
   */
  public onDisaplayKeys() {
    const url = 'http://localhost:8080/displayKeys';
    this.http.get(url,
      { responseType: 'text' }).subscribe(
        res => {
          this.text = res;
          console.log(res);
        },
      );
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
      { responseType: 'text' }).subscribe(
        res => {
          this.text = res;
          console.log(res);
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
    const url = 'http://localhost:8080/sign';
    this.http.get(url,
      { responseType: 'text' }).subscribe(
        res => {
          this.text = res;
          console.log(res);
        },
      );
  }

  /**
   * Generates a report summarizing the status of the HSM. A list of the registered
   * users and stored keys.
   */
  public onGenReport() {
    const url = 'http://localhost:8080/genReport';
    this.http.get(url,
      { responseType: 'text' }).subscribe(
        res => {
          this.text = res;
          console.log(res);
        },
      );
  }

  // TODO: On register user, toast their success.
  public onRegisterUser() {
    const data = 'This is from angular';
    const url = 'http://localhost:8080/registerUser';
    this.http.get<string>(url,
      {
        params: new HttpParams().set('id', data)
      },
      ).subscribe(
        res => {
          const returnValues = Object.values(res);
          console.log(returnValues);
          this.text = returnValues[1];

        },
      );
  }

  /**
   *  Basic architecture of sending and receiving data.
   */
  public onTest() {
    const data = 'This is from angular';
    const url = 'http://localhost:8080/test';
    this.http.get<string>(url,
      {
        params: new HttpParams().set('id', data)
      },
      ).subscribe(
        res => {
          const returnValues = Object.values(res);
          console.log(returnValues[1]);
          this.text = returnValues[1];
        },
      );
  }

}
