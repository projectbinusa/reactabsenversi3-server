import React, { useEffect, useState } from "react";
import { Tabs } from "flowbite-react";
import { HiAdjustments, HiClipboardList, HiUserCircle } from "react-icons/hi";
import { MdDashboard } from "react-icons/md";
import Navbar from "../../components/NavbarSuper";
import Sidebar from "../../components/SidebarUser";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPenToSquare } from "@fortawesome/free-regular-svg-icons";
import { faEye, faEyeSlash } from "@fortawesome/free-solid-svg-icons";
import axios from "axios";
import Loader from "../../components/Loader";
import Swal from "sweetalert2";
import { API_DUMMY } from "../../utils/api";

function ProfilSA() {
  const [showPassword, setShowPassword] = useState(false);
  const [imageSuperAdmin, setImageAdmin] = useState("");
  const [showPasswordd, setShowPasswordd] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [ubahUsername, setUbahUsername] = useState(false);
  const [profile, setProfile] = useState([]);
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(false);
  const token = localStorage.getItem("token");
  const id = localStorage.getItem("superadminId");
  const [selectedFile, setSelectedFile] = useState(null);
  const [preview, setPreview] = useState(null);
  const [edit, setEdit] = useState(false);
  const [passwordLama, setPasswordLama] = useState("");
  const [passwordBaru, setPasswordBaru] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const editPassword = async (e) => {
    e.preventDefault();

    if (passwordBaru !== confirmPassword) {
      Swal.fire(
        "Gagal",
        "Password baru dan konfirmasi password tidak cocok",
        "error"
      );
      return;
    }

    try {
      const response = await axios.put(
        `${API_DUMMY}/api/superadmin/edit-password/${id}`,
        {
          old_password: passwordLama,
          new_password: passwordBaru,
          confirm_new_password: confirmPassword,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      Swal.fire("Berhasil", "Password berhasil diubah", "success");
      window.location.reload();
    } catch (error) {
      console.log(error);
      Swal.fire("Error", "Terjadi kesalahan, coba lagi nanti", "error");
    }
  };

  const getProfile = async () => {
    try {
      const response = await axios.get(
        `${API_DUMMY}/api/superadmin/getbyid/${id}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setProfile(response.data);
      setImageAdmin(response.data.imageSuperAdmin);
      setEmail(response.data.email);
      setUsername(response.data.username);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  const HandleUbahUsernameEmail = async (e) => {
    e.preventDefault();
    const usMail = {
      email: email,
      username: username,
    };
    try {
      const response = await axios.put(
        `${API_DUMMY}/api/superadmin/edit-email-username/${id}`,
        usMail,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setProfile(response.data);
      setUsername(response.data.username);
      setEmail(response.data.email);
      Swal.fire("Berhasil", "Berhasil mengubah username dan email", "success");
      setTimeout(() => {
        Swal.fire("Info", "Silahkan login kembali", "info");
        setTimeout(() => {
          window.location.href = "/";
        }, 1000);
      }, 2000);
    } catch (error) {
      console.error("Error updating data:", error);
      Swal.fire("Gagal", "Gagal mengubah username dan email", "error");
    }
  };

  useEffect(() => {
    getProfile();
  }, []);

  const handleFileChange = (event) => {
    const file = event.target.files[0];
    setSelectedFile(file);
    setPreview(URL.createObjectURL(file));
  };

  const handleImageUpload = async () => {
    if (!selectedFile) {
      Swal.fire("Error", "No file selected", "error");
      return;
    }

    setLoading(true);
    const formData = new FormData();
    formData.append("image", selectedFile);

    try {
      const response = await axios.put(
        `${API_DUMMY}/api/superadmin/ubah-foto/${id}`,
        formData,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "multipart/form-data",
          },
        }
      );
      setLoading(false);
      setImageAdmin(response.data.imageSuperAdmin);
      Swal.fire("Berhasil", "Berhasil mengubah foto profil", "success", {
        timer: 2000,
      });
      window.location.reload();
    } catch (error) {
      setLoading(false);
      console.error("Error uploading image:", error);
      Swal.fire("Error", "Error uploading image", "error");
    }
  };
  return (
    <>
      {loading && <Loader />}
      <div className="flex flex-col h-screen">
        <Navbar />
        <div className="flex h-full">
          <Sidebar />
          <div className="content-page container p-8 min-h-screen ml-0 md:ml-64 mt-20">
            <Tabs aria-label="Tabs with underline" style="underline">
              <Tabs.Item active title="Profile" icon={HiUserCircle}>
                {/* Konten tab Profil */}
                <div className="font-medium text-gray-800 dark:text-white">
                  <div className="profile mt-12 bg-white p-5 rounded-xl shadow-xl border border-gray-300">
                    <h2 className="text-xl font-bold">Profile Picture</h2>
                    <div className="flex flex-col items-center mt-4">
                      {/* Placeholder untuk menampilkan gambar profil yang dipilih */}
                      <img
                        src={
                          preview
                            ? preview
                            : imageSuperAdmin
                            ? imageSuperAdmin
                            : "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAALcAAACUCAMAAADmiEg1AAAAllBMVEXw8PAAAAD4+Pj09PQNFSYACiDZ2ts8PD25urv///+kpaYAFigAECQAABkAABUAEyYAAA4AAAjl5OTp6urR0tOMjY98gIMAABx2d3hZW2A2O0UjKzaeoKIfIixFRERgZWtQU1dBRUpvb3HFxcYsLDOXmJkADRkoKyxMTU41NTVjZmcaIC4iIyavs7QwMzoYGx8VGiQPEhgcRQ3YAAAE20lEQVR4nO2af3OiPBDHya5BIiHhl4CoVUCvrVavz/t/c0+wdzO9u1YoXE16k88fjtPizNd1881mN45jsVgsFovFYrFYLBaLxWKxWCyWLw2+oFvGh0DAJHZb4kS91y2nJwixtyoW5fK4LBfFynO/hHJkabY9RmFAKac0CKPjNkuZ8cohqZ8CpTkPSUuYU0rFUx0z3cKugs6KRJKK9d1mf2rS5rTf3K0FlSFZOQaHHNyaSEnKzEsYACAAY4mXle0faxd0y3sPSLeRH4Rz97WHqPfuPBR+tE0NFY5upeRNG/xdH2JTEV9UrpGpgvFZ+GHtvBVVcOrQF8vYROGwJT7J3nE8ZBlRqWJgprC9kl2/u7EjFur/e+PsENIdF4sriYDxQvCdcWsTM0HXzTVV0KypKG4mqB/YrHkwvx5MmAd83Ri2NOeE7zp8Dt0dJ/Mb6ekHxiUNs65Fx7KQlkZ5ITbKLDo3cnDVU0YlCqg0od0ex6hKFJMcBaq8O00uiZJXJlk4CE5O3YGEE+HCpHgnKnHT7sTFVD0X30BPTzBu9fTQ3fO5W2F13xbsm9+uWbodiCTxeviJR2RkkGyHlXmU9dCdRfRslH8rQcse+86S9vl6twO9Pol7WZaeSXmC8VFVHp314J7wo0nL0rnU3/cdkjB55CS7kZ6egDfhdN9x3tlTPunhOrcEk4Og5VULx7Sk4pCYlSYq4N+lqK80L9EphPxuWLgVrIj8a4cCdbTwo8Ik8/4BTIVPZu80fhBnxBdT46LttJ2fc+CTffJmfzDZEz84G9f1uYDefSC/1e6fucDc+psMnozacl4B3n0ug8me/ZIsiGw1CWR+b96a/AlLHyIuyXHmJgiA2L4k7uxIJI8eUgPX5E8gLp5Dn5LneuU1aZo23qp+JtQXz0VsbLRb0Gk2JPC5IOG6rMp1SAT3A7LwTB5LXVDKS0ICzmkL5wEhZWO86hZgyWwzfdyt1+vd43QzS5jRKfIKBOa4jed5jesww6fcwH7xi4uZwG8XIsC8yLPm0FHGtv6+acwyQ3Tmofivo0OI6b0I5yYtUYgPRFLRsSFi+kwlOZhj5JBuVbE3mXVFEk5ClYzGzLshrZSch6vDtB9Pvsy7zRCOl/q17vXzY1y39awJg3p0d8IPs54ujU4W+qJr8nYDMNmq81lf2e2WdBnUa3cVyFrZH5ChIq6E655OwYnK8GPNBUwOoaQ9pkGfCKZTSqsP3kACt6LBtEe7/PNA9Zt3bTd/wk7KNzONujENJRnQE4GCyFBjwFkleDSgc4YJ4ULfABbaqfxsyAKDWTsQ0rU0YZEH1aB+NsYVzReadLdXk6LOYuptcBVpu7QE85xWA1dX21TO9Ww+av8IRDHUFLAQgZ5mODRLOhm87cFsQpc9St+/j/IE/jh4yoTuIx/mRWNJsjB/GO7B7IFGOi6JYbwIos7R3/uwLAyu3ZP8LNClnIxIUPAIn2g4P4y923AZHuvQ3Y62Rxxb0NFzKQ9ORJIxpRFTn9dwemhtMBilO+h1B+5v0+qWo3RzLQaudNNxuuUX1a0v3r7VfTNe8gSHoy+/uZ+4w0l8Xbp9fjcdzh0feKgerVtKOgYpdejG9Mwn4+BnHb0fTL2x6GlZtQPKcehuglssFovFYrFYLBaLxWKxWCwWi+Wf5X+cg0tu3tqVfwAAAABJRU5ErkJggg=="
                        }
                        alt="Profile"
                        className="w-48 h-48 rounded-full"
                      />
                      {/* Pesan instruksi */}
                      <p className="mt-2 text-sm text-gray-600">
                        JPG atau PNG tidak lebih besar dari 5 MB. Disarankan
                        Berukuran 1:1.
                      </p>
                    </div>
                    <div className="flex justify-between mt-6">
                      <div>
                        <label htmlFor="fileInput" className="cursor-pointer">
                          <span className="z-20 block rounded-xl border-2 border-white bg-blue-100 p-4 text-blue-700 active:bg-blue-50">
                            <FontAwesomeIcon icon={faPenToSquare} />
                          </span>
                        </label>
                        {/* Input file tersembunyi */}
                        <input
                          id="fileInput"
                          type="file"
                          accept="image/*"
                          onChange={handleFileChange}
                          className="hidden"
                        />
                      </div>
                      <button
                        type="submit"
                        className="z-20 block rounded-xl border-2 border-white bg-blue-100 p-4 text-blue-700 active:bg-blue-50"
                        onClick={handleImageUpload}
                        disabled={loading || !selectedFile}
                      >
                        {loading ? "Uploading..." : "Simpan"}
                      </button>
                    </div>
                  </div>
                </div>
              </Tabs.Item>

              <Tabs.Item title="Detail" icon={MdDashboard}>
                {/* Konten tab Dashboard */}
                <div className="font-medium text-gray-800 dark:text-white">
                  <div className="detail-akun mt-12 bg-white p-5 rounded-xl shadow-lg border border-gray-300">
                    <p className="text-lg sm:text-xl font-medium mb-4 sm:mb-7">
                      Detail Akun
                    </p>
                    <form onSubmit={HandleUbahUsernameEmail}>
                      <div className="relative mb-3">
                        <label className="block mb-2 text-sm sm:text-xs font-medium text-gray-900">
                          Nama Lengkap
                        </label>
                        <input
                          type="text"
                          id="nama"
                          className="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 text-sm sm:text-xs rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
                          placeholder="Masukkan Nama"
                          value={username}
                          onChange={(e) => setUsername(e.target.value)}
                          required
                          disabled={!ubahUsername}
                        />
                      </div>
                      <div className="relative">
                        <label className="block mb-2 text-sm sm:text-xs font-medium text-gray-900">
                          Email
                        </label>
                        <input
                          type="email"
                          id="email"
                          className="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 text-sm sm:text-xs rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
                          placeholder="Masukkan Email"
                          value={email}
                          onChange={(e) => setEmail(e.target.value)}
                          required
                          disabled={!ubahUsername}
                        />
                      </div>

                      <div className="flex justify-between mt-6">
                        {!ubahUsername && (
                          <button
                            type="button"
                            onClick={() => setUbahUsername(true)}
                            className="z-20 block rounded-xl border-2 border-white bg-blue-100 p-4 text-blue-700 active:bg-blue-50"
                          >
                            Ubah
                          </button>
                        )}
                        {ubahUsername && (
                          <>
                            <button
                              type="button"
                              onClick={() => setUbahUsername(false)}
                              className="z-20 block rounded-xl border-2 border-white bg-rose-100 p-4 text-rose-500 active:bg-rose-50"
                            >
                              Batal
                            </button>

                            <button
                              type="submit"
                              className="z-20 block rounded-xl border-2 border-white bg-blue-100 p-4 text-blue-700 active:bg-blue-50"
                            >
                              Simpan
                            </button>
                          </>
                        )}
                      </div>
                    </form>
                  </div>
                </div>
              </Tabs.Item>
              <Tabs.Item title="Settings" icon={HiAdjustments}>
                <div className="font-medium text-gray-800 dark:text-white">
                  <div className="settings mt-12 bg-white p-5 rounded-xl shadow-lg border border-gray-300">
                    <p className="text-lg sm:text-xl font-medium mb-4 sm:mb-7">
                      Settings
                    </p>
                    <form onSubmit={editPassword}>
                      <div className="relative mb-3">
                        <label className="block mb-2 text-sm sm:text-xs font-medium text-gray-900">
                          Password Lama
                        </label>
                        <input
                          type={showPasswordd ? "text" : "password"}
                          id="pw-lama"
                          className="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 text-sm sm:text-xs rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 "
                          required
                          value={passwordLama}
                          onChange={(e) => setPasswordLama(e.target.value)}
                        />
                        <FontAwesomeIcon
                          icon={showPasswordd ? faEye : faEyeSlash}
                          className="absolute top-1/2 right-4 transform -translate-y-1/2 cursor-pointer mt-3"
                          onClick={() => setShowPasswordd(!showPasswordd)}
                        />
                      </div>
                      <div className="relative mb-3">
                        <label className="block mb-2 text-sm sm:text-xs font-medium text-gray-900">
                          Password Baru
                        </label>
                        <div className="relative">
                          <input
                            type={showPassword ? "text" : "password"}
                            id="pw-baru"
                            className="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 text-sm sm:text-xs rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 "
                            required
                            value={passwordBaru}
                            onChange={(e) => setPasswordBaru(e.target.value)}
                          />
                          <FontAwesomeIcon
                            icon={showPassword ? faEye : faEyeSlash}
                            className="absolute top-1/2 right-4 transform -translate-y-1/2 cursor-pointer"
                            onClick={() => setShowPassword(!showPassword)}
                          />
                        </div>
                      </div>
                      <div className="relative mb-3">
                        <label className="block mb-2 text-sm sm:text-xs font-medium text-gray-900">
                          Konfirmasi Password Baru
                        </label>
                        <div className="relative">
                          <input
                            type={showConfirmPassword ? "text" : "password"}
                            id="konfirmasi-pw"
                            className="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 text-sm sm:text-xs rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 "
                            required
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                          />
                          <FontAwesomeIcon
                            icon={showConfirmPassword ? faEye : faEyeSlash}
                            className="absolute top-1/2 right-4 transform -translate-y-1/2 cursor-pointer"
                            onClick={() =>
                              setShowConfirmPassword(!showConfirmPassword)
                            }
                          />
                        </div>
                      </div>

                      <div className="flex justify-between mt-6">
                        <button
                          type="submit"
                          className="z-20 block rounded-xl border-2 border-white bg-blue-100 p-4 text-blue-700 active:bg-blue-50"
                        >
                          Simpan
                        </button>
                      </div>
                    </form>
                  </div>
                </div>
              </Tabs.Item>
            </Tabs>
          </div>
        </div>
      </div>
    </>
  );
}

export default ProfilSA;
