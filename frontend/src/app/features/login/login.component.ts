import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import {AuthService} from '../../core/services/auth.service';
import {NgClass} from '@angular/common';
import {StorageService} from '../../core/services/storage.service';
import {ACCESS_TOKEN, REFRESH_TOKEN} from '../../core/constants/auth.constants';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, NgClass],
  templateUrl: './login.component.html'
})
export class LoginComponent {


  loginForm = new FormGroup({
    username: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required, Validators.minLength(6)])
  });

  showPassword = false;
  showErrorMessage = false;

  constructor(private authService: AuthService,
              private router: Router,
              private storage : StorageService,) {}

  onSubmit() {
    this.showErrorMessage = false;
    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.value).subscribe({
        next: data => {
          this.storage.save(ACCESS_TOKEN,data.accessToken )
          this.storage.save(REFRESH_TOKEN, data.refreshToken)
          this.router.navigate(['/workspace/dashboard'])
        },
        error: (err) => {
          this.showErrorMessage = true;
          console.error("Erreur de connexion", err)
        }
      });
    }
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }


}
