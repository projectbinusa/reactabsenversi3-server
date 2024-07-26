import React, { useEffect, useState } from "react";
import Navbar from "../../../components/NavbarAdmin";
import Sidebar from "../../../components/SidebarUser";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faArrowLeft,
  faFloppyDisk,
  faPenToSquare,
} from "@fortawesome/free-solid-svg-icons";
import { useParams } from "react-router-dom";
import axios from "axios";
import Swal from "sweetalert2";
import Loader from "../../../components/Loader";
import { API_DUMMY } from "../../../utils/api";

function EditOrganisasi() {
  const { id } = useParams();
  const [namaOrganisasi, setNamaOrganisasi] = useState("");
  const [alamat, setAlamat] = useState("");
  const [nomerTelepon, setNomerTelepon] = useState("");
  const [emailOrganisasi, setEmailOrganisasi] = useState("");
  const [kecamatan, setKecamatan] = useState("");
  const [kabupaten, setKabupaten] = useState("");
  const [provinsi, setProvinsi] = useState("");
  const [fotoOrganisasi, setFotoOrganisasi] = useState(null);
  const [fotoUrl, setFotoUrl] = useState("");
  const [loading, setLoading] = useState(false);
  const idAdmin = localStorage.getItem("adminId");

  useEffect(() => {
    const fetchData = async () => {
      try {
        const token = localStorage.getItem("token");
        const config = {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        };

        const response = await axios.get(
          `${API_DUMMY}/api/organisasi/getById/${id}`,
          config
        );
        const dataOrganisasi = response.data;

        setNamaOrganisasi(dataOrganisasi.namaOrganisasi);
        setAlamat(dataOrganisasi.alamat);
        setEmailOrganisasi(dataOrganisasi.emailOrganisasi);
        setNomerTelepon(dataOrganisasi.nomerTelepon);
        setKecamatan(dataOrganisasi.kecamatan);
        setKabupaten(dataOrganisasi.kabupaten);
        setProvinsi(dataOrganisasi.provinsi);
        setFotoUrl(dataOrganisasi.fotoOrganisasi);
      } catch (error) {
        alert("Terjadi kesalahan: " + error);
      }
    };

    fetchData();
  }, [id]);

  const handleInputChange = (setter) => (event) => {
    setter(event.target.value);
  };

  const fotoOrganisasiChangeHandler = (event) => {
    setFotoOrganisasi(event.target.files[0]);
    setFotoUrl("");
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const organisasiData = {
      namaOrganisasi: namaOrganisasi,
      alamat: alamat,
      nomerTelepon: nomerTelepon,
      emailOrganisasi: emailOrganisasi,
      kecamatan: kecamatan,
      kabupaten: kabupaten,
      provinsi: provinsi,
    };

    const formData = new FormData();
    formData.append(
      "organisasi",
      new Blob([JSON.stringify(organisasiData)], { type: "application/json" })
    );
    if (fotoOrganisasi) {
      formData.append("image", fotoOrganisasi);
    }

    try {
      const token = localStorage.getItem("token");
      const config = {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "multipart/form-data",
        },
      };

      setLoading(true);
      await axios.put(
        `${API_DUMMY}/api/organisasi/editById/${id}?idAdmin=${idAdmin}`,
        formData,
        config
      );
      setLoading(false);

      Swal.fire("Berhasil", "Organisasi berhasil diperbarui!", "success");
      window.location.href = "/admin/organisasi";
    } catch (error) {
      setLoading(false);
      Swal.fire(
        "Gagal",
        "Terjadi kesalahan saat memperbarui organisasi",
        "error"
      );
    }
  };

  return (
    <div className="flex flex-col h-screen">
      {loading && <Loader />}
      <div className="sticky top-0 z-50">
        <Navbar />
      </div>
      <div className="flex h-full">
        <div className="fixed">
          <Sidebar />
        </div>
        <div className="sm:ml-64 content-page container p-8 ml-14 md:ml-64 mt-12">
          <div className="p-4">
            <div className="p-5">
              <div className="w-full p-4 text-center bg-white border border-gray-200 rounded-lg shadow sm:p-8 dark:bg-gray-800 dark:border-gray-700">
                <div className="flex justify-between">
                  <h6 className="mb-2 text-xl font-bold text-gray-900 dark:text-white">
                    Edit Organisasi
                  </h6>
                </div>
                <hr />
                <div className="mt-5 text-left">
                  <form
                    id="updateForm"
                    onSubmit={handleSubmit}
                    encType="multipart/form-data"
                  >
                    <div className="mt-5 text-center">
                      <img
                        className="mb-5 rounded-full w-48 h-48 mx-auto"
                        src={
                          fotoOrganisasi
                            ? URL.createObjectURL(fotoOrganisasi)
                            : fotoUrl
                        }
                        alt="Foto Organisasi"
                      />
                      <div className="flex justify-center mt-4">
                        <label htmlFor="fileInput" className="cursor-pointer">
                          <span className="z-20 block rounded-xl border-2 border-white bg-blue-100 p-4 text-blue-700 active:bg-blue-50">
                            <FontAwesomeIcon icon={faPenToSquare} />
                          </span>
                        </label>
                        <input
                          id="fileInput"
                          type="file"
                          accept="image/*"
                          onChange={fotoOrganisasiChangeHandler}
                          className="hidden"
                        />
                      </div>
                    </div>
                    <input type="hidden" name="id_organisasi" value={id} />
                    <div className="grid md:grid-cols-2 md:gap-6 mt-5">
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="text"
                          name="nama_organisasi"
                          id="nama_organisasi"
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                          placeholder=" "
                          autoComplete="off"
                          required
                          value={namaOrganisasi}
                          onChange={handleInputChange(setNamaOrganisasi)}
                        />
                        <label
                          htmlFor="nama_organisasi"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Nama Organisasi
                        </label>
                      </div>
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="text"
                          name="alamat"
                          id="alamat"
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                          placeholder=" "
                          autoComplete="off"
                          required
                          value={alamat}
                          onChange={handleInputChange(setAlamat)}
                        />
                        <label
                          htmlFor="alamat"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Alamat
                        </label>
                      </div>
                    </div>
                    <div className="grid md:grid-cols-2 md:gap-6">
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="text"
                          name="kecamatan"
                          id="kecamatan"
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                          placeholder=" "
                          autoComplete="off"
                          required
                          value={kecamatan}
                          onChange={handleInputChange(setKecamatan)}
                        />
                        <label
                          htmlFor="kecamatan"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Kecamatan
                        </label>
                      </div>
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="text"
                          name="kabupaten"
                          id="kabupaten"
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                          placeholder=" "
                          autoComplete="off"
                          required
                          value={kabupaten}
                          onChange={handleInputChange(setKabupaten)}
                        />
                        <label
                          htmlFor="kabupaten"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Kabupaten
                        </label>
                      </div>
                    </div>
                    <div className="grid md:grid-cols-2 md:gap-6">
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="text"
                          name="provinsi"
                          id="provinsi"
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                          placeholder=" "
                          autoComplete="off"
                          required
                          value={provinsi}
                          onChange={handleInputChange(setProvinsi)}
                        />
                        <label
                          htmlFor="provinsi"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Provinsi
                        </label>
                      </div>
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="email"
                          name="email_organisasi"
                          id="email_organisasi"
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                          placeholder=" "
                          autoComplete="off"
                          required
                          value={emailOrganisasi}
                          onChange={handleInputChange(setEmailOrganisasi)}
                        />
                        <label
                          htmlFor="email_organisasi"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Email
                        </label>
                      </div>
                    </div>
                    <div className="relative z-0 w-full mb-6 group">
                      <input
                        type="text"
                        name="nomer_telepon"
                        id="nomer_telepon"
                        className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                        placeholder=" "
                        autoComplete="off"
                        required
                        value={nomerTelepon}
                        onChange={handleInputChange(setNomerTelepon)}
                      />
                      <label
                        htmlFor="nomer_telepon"
                        className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                      >
                        Nomer Telepon
                      </label>
                    </div>
                    <div className="flex justify-between">
                      <a
                        href="/admin/organisasi"
                        className="text-white focus:ring-4 font-medium rounded-lg text-sm px-5 py-2.5 mb-2 bg-red-600 hover:bg-red-800 focus:outline-none focus:ring-red-300"
                      >
                        <FontAwesomeIcon icon={faArrowLeft} /> &nbsp;Batal
                      </a>
                      <button
                        type="submit"
                        className="text-white focus:ring-4 font-medium rounded-lg text-sm px-5 py-2.5 mb-2 bg-blue-600 hover:bg-blue-800 focus:outline-none focus:ring-blue-300"
                      >
                        <FontAwesomeIcon icon={faFloppyDisk} /> &nbsp;Simpan
                      </button>
                    </div>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default EditOrganisasi;
